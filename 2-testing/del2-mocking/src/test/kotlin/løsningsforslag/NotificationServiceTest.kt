package løsningsforslag

import assertk.assertThat
import assertk.assertions.any
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import services.email.BatchResult
import services.email.EmailService
import services.notification.NotificationEvent
import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.InMemoryLogger
import services.LogEntry
import services.LogLevel
import services.notification.NotificationService
import services.order.InMemoryOrderRepository
import kotlin.text.contains

class NotificationServiceTest {

    private lateinit var emailService: EmailService
    private lateinit var orderRepository: InMemoryOrderRepository
    private lateinit var logger: InMemoryLogger
    private lateinit var notificationService: NotificationService
    
    private val testOrder = Order(
        id = "ORDER-123",
        customerId = "CUST-1",
        customerEmail = "customer@example.com",
        items = listOf(
            OrderItem(
                Product("PROD-1", "Test Product", 100),
                quantity = 2
            )
        ),
        status = OrderStatus.PENDING
    )
    
    @BeforeEach
    fun setup() {
        emailService = mockk()
        orderRepository = InMemoryOrderRepository()
        logger = InMemoryLogger()
        notificationService = NotificationService(emailService, orderRepository, logger)

        orderRepository.save(testOrder)
    }
    
    @Test
    fun `skal sende e-post ved ordre-bekreftelse`() {
        // Arrange
        every { emailService.send(any()) } returns true
        
        // Act
        val result = notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        assertThat(result).isTrue()
        verify(exactly = 1) {
            emailService.send(
                match { email ->
                    email.recipient == "customer@example.com" &&
                    email.subject.contains(testOrder.id)
                }
            )
        }
    }
    
    @Test
    fun `skal oppdatere ordre-status til CONFIRMED etter vellykket e-post`() {
        // Arrange
        every { emailService.send(any()) } returns true
        
        // Act
        notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        val updatedOrder = orderRepository.findById(testOrder.id)
        assertThat(updatedOrder).isNotNull()
        assertThat(updatedOrder!!.status).isEqualTo(OrderStatus.CONFIRMED)
    }
    
    @Test
    fun `skal logge info når ordre-bekreftelse sendes`() {
        // Arrange
        every { emailService.send(any()) } returns true
        
        // Act
        notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        assertThat(logger.logs).any {
            it.prop(LogEntry::level).isEqualTo(LogLevel.INFO)
            it.prop(LogEntry::message).contains("confirmed")
        }
    }
    
    @Test
    fun `skal returnere false og ikke oppdatere status hvis e-post feiler`() {
        // Arrange
        every { emailService.send(any()) } returns false
        
        // Act
        val result = notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        assertThat(result).isFalse()
        
        val order = orderRepository.findById(testOrder.id)
        assertThat(order!!.status).isEqualTo(OrderStatus.PENDING)
        
        assertThat(logger.logs).any {
            it.prop(LogEntry::level).isEqualTo(LogLevel.ERROR)
            it.prop(LogEntry::message).contains("Failed to send")
        }
    }
    
    @Test
    fun `skal håndtere exception fra EmailService`() {
        // Arrange
        every { emailService.send(any()) } throws RuntimeException("Network error")
        
        // Act
        val result = notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        assertThat(result).isFalse()
        assertThat(logger.logs).any {
            it.prop(LogEntry::level).isEqualTo(LogLevel.ERROR)
            it.prop(LogEntry::throwable).isNotNull()
        }
    }
    
    @Test
    fun `skal returnere false hvis ordre ikke finnes`() {
        // Act
        val result = notificationService.notifyOrderConfirmed("NON-EXISTENT")
        
        // Assert
        assertThat(result).isFalse()
        verify(exactly = 0) { emailService.send(any()) }
        assertThat(logger.logs).any {
            it.prop(LogEntry::level).isEqualTo(LogLevel.ERROR)
            it.prop(LogEntry::message).contains("not found")
        }
    }
    
    @Test
    fun `skal ikke sende e-post hvis ordre ikke er i PENDING status`() {
        // Arrange
        val confirmedOrder = testOrder.confirm()
        orderRepository.save(confirmedOrder)
        
        // Act
        val result = notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert
        assertThat(result).isFalse()
        verify(exactly = 0) { emailService.send(any()) }
        assertThat(logger.logs).any {
            it.prop(LogEntry::level).isEqualTo(LogLevel.WARN)
            it.prop(LogEntry::message).contains("not in PENDING status")
        }
    }
    
    @Test
    fun `skal sende kansellerings-e-post med årsak`() {
        // Arrange
        every { emailService.send(any()) } returns true
        val cancellationReason = "Customer requested cancellation"
        
        // Act
        val result = notificationService.notifyOrderCancelled(testOrder.id, cancellationReason)
        
        // Assert
        assertThat(result).isTrue()
        verify(exactly = 1) {
            emailService.send(
                match { email ->
                    email.body.contains(cancellationReason)
                }
            )
        }
        
        val cancelledOrder = orderRepository.findById(testOrder.id)
        assertThat(cancelledOrder!!.status).isEqualTo(OrderStatus.CANCELLED)
    }
    
    @Test
    fun `skal sende batch-notifikasjoner`() {
        // Arrange
        val order2 = testOrder.copy(id = "ORDER-124", customerEmail = "customer2@example.com")
        val order3 = testOrder.copy(id = "ORDER-125", customerEmail = "customer3@example.com")
        orderRepository.save(order2)
        orderRepository.save(order3)
        
        every { emailService.sendBatch(any()) } returns BatchResult(successful = 3, failed = 0)
        
        // Act
        val result = notificationService.notifyBatchOrders(
            listOf(testOrder.id, order2.id, order3.id),
            NotificationEvent.ORDER_CONFIRMED
        )
        
        // Assert
        assertThat(result.successful).isEqualTo(3)
        assertThat(result.failed).isEqualTo(0)
        verify(exactly = 1) {
            emailService.sendBatch(match { it.size == 3 })
        }
        assertThat(logger.logs.size).isGreaterThanOrEqualTo(2)
        assertThat(logger.logs[0].message).contains("Processing batch notification for 3 orders, event: ORDER_CONFIRMED")
        assertThat(logger.logs.last().message).contains("Batch notification completed: 3 successful, 0 failed")
    }
    
    @Test
    fun `skal verifisere rekkefølge på kall til logger`() {
        // Arrange
        val logger = spyk(InMemoryLogger())
        val notificationService = NotificationService(emailService, orderRepository, logger)
        every { emailService.send(any()) } returns true
        
        // Act
        notificationService.notifyOrderConfirmed(testOrder.id)
        
        // Assert - sjekk at loggene kommer i riktig rekkefølge
        verifyOrder {
            logger.info("Processing order confirmation notification for order {}", testOrder.id)
            logger.info("Order {} confirmed and notification sent to {}", testOrder.id, testOrder.customerEmail)
        }
    }
    
    @Test
    fun `skal bruke relaxed mock for å teste kun det som er viktig`() {
        // Arrange - relaxed mock returnerer default-verdier
        val relaxedEmailService = mockk<EmailService>(relaxed = true)
        val serviceWithRelaxed = NotificationService(relaxedEmailService, orderRepository, logger)
        
        // Act
        serviceWithRelaxed.notifyOrderConfirmed(testOrder.id)
        
        // Assert - vi trenger kun verifisere at send ble kalt
        verify { relaxedEmailService.send(any()) }
    }
}