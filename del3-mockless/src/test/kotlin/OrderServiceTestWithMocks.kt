import assertk.assertThat
import assertk.assertions.*
import domain.*
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import services.inventory.InventoryService
import services.notification.NotificationService
import services.order.*
import services.pricing.PricingService

/*
 * Oppgave: Refaktorer til mockless testing
 *
 * 1. Erstatt mocks med fake repositories (InMemoryOrderRepository, InMemoryInventoryService, InMemoryPricingService)
 * 2. Lag builder functions for testdata (aProduct, anOrderItem, anOrder, aDiscount)
 * 3. Test faktisk oppførsel, ikke implementasjon
 * 4. Identifiser hva som IKKE kan fakes (hint: NotificationService er ekstern)
 *
 * Sammenlign med løsningsforslag/OrderServiceTest.kt for å se hvordan det kan gjøres!
 */

class OrderServiceTestWithMocks {
    
    private lateinit var orderRepository: OrderRepository
    private lateinit var inventoryService: InventoryService
    private lateinit var pricingService: PricingService
    private lateinit var notificationService: NotificationService
    private lateinit var orderService: OrderService
    
    @BeforeEach
    fun setup() {
        // TODO: Erstatt disse mocksene med in-memory implementasjoner
        orderRepository = mockk()
        inventoryService = mockk()
        pricingService = mockk()
        notificationService = mockk()
        
        orderService = OrderService(
            orderRepository,
            inventoryService,
            pricingService,
            notificationService
        )
    }
    
    @Test
    fun `skal opprette ordre når produkter er på lager`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Laptop", price = 10000)
        val items = listOf(OrderItem(product, quantity = 2))
        
        // TODO: Disse every-blokkene tester implementasjonsdetaljer
        // Refaktorer til å teste oppførsel i stedet
        every { inventoryService.isAvailable("PROD-1", 2) } returns true
        every { inventoryService.reserve("PROD-1", 2) } just Runs
        every { orderRepository.save(any()) } returnsArgument 0
        
        // Act
        val result = orderService.createOrder("CUST-1", "customer@example.com", items)
        
        // Assert
        // TODO: Disse verify-kallene tester implementasjon, ikke oppførsel
        assertThat(result.isSuccess).isTrue()
        verify { inventoryService.reserve("PROD-1", 2) }
        verify { orderRepository.save(any()) }
    }
    
    @Test
    fun `skal feile hvis produkter ikke er på lager`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val items = listOf(OrderItem(product, quantity = 10))
        
        every { inventoryService.isAvailable("PROD-1", 10) } returns false
        
        // Act
        val result = orderService.createOrder("CUST-1", "customer@example.com", items)
        
        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InsufficientInventoryException::class)
        
        // TODO: Denne verifiseringen tester implementasjon
        verify(exactly = 0) { inventoryService.reserve(any(), any()) }
    }
    
    @Test
    fun `skal bekrefte ordre og oppdatere status`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            status = OrderStatus.PENDING
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { orderRepository.save(any()) } returnsArgument 0
        every { inventoryService.confirm(any(), any()) } just Runs
        every { notificationService.notifyOrderConfirmed(any()) } returns true

        // Act
        val result = orderService.confirmOrder("ORDER-1")

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.status).isEqualTo(OrderStatus.CONFIRMED)

        // TODO: Tester implementasjonsdetaljer
        verify { inventoryService.confirm(any(), any()) }
        verify { notificationService.notifyOrderConfirmed("ORDER-1") }
    }

    @Test
    fun `skal ikke kunne bekrefte ordre som ikke er PENDING`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            status = OrderStatus.CONFIRMED
        )

        every { orderRepository.findById("ORDER-1") } returns order

        // Act
        val result = orderService.confirmOrder("ORDER-1")

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InvalidOrderStateException::class)

        // TODO: Med mocks må vi verifisere at confirm IKKE ble kalt
        verify(exactly = 0) { inventoryService.confirm(any(), any()) }
    }

    @Test
    fun `skal kansellere ordre og frigi inventory`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 2)),
            status = OrderStatus.PENDING
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { orderRepository.save(any()) } returnsArgument 0
        every { inventoryService.release("PROD-1", 2) } just Runs
        every { notificationService.notifyOrderCancelled(any(), any()) } returns true

        // Act
        val result = orderService.cancelOrder("ORDER-1", "Customer requested cancellation")

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.status).isEqualTo(OrderStatus.CANCELLED)

        // TODO: Med mocks må vi verifisere at release ble kalt - tester implementasjon
        verify { inventoryService.release("PROD-1", 2) }
        verify { notificationService.notifyOrderCancelled("ORDER-1", "Customer requested cancellation") }
    }

    @Test
    fun `skal kunne legge til rabattkode på PENDING ordre`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 10000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            status = OrderStatus.PENDING
        )
        val discount = Discount(
            code = "SUMMER2025",
            type = DiscountType.PERCENTAGE,
            value = 10,
            minOrderAmount = 1000
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { orderRepository.save(any()) } returnsArgument 0
        every { pricingService.validateDiscount("SUMMER2025", order) } returns discount

        // Act
        val result = orderService.applyDiscount("ORDER-1", "SUMMER2025")

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.discountCode).isEqualTo("SUMMER2025")

        // TODO: Må verifisere alle mock-interaksjoner
        verify { pricingService.validateDiscount("SUMMER2025", any()) }
    }

    @Test
    fun `skal ikke kunne legge til ugyldig rabattkode`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            status = OrderStatus.PENDING
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { pricingService.validateDiscount("INVALID", order) } returns null

        // Act
        val result = orderService.applyDiscount("ORDER-1", "INVALID")

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InvalidDiscountException::class)

        // TODO: Verifiserer at validateDiscount returnerte null - implementasjonsdetalj
        verify { pricingService.validateDiscount("INVALID", any()) }
    }

    @Test
    fun `skal ikke kunne legge til rabatt hvis ordre under minimum beløp`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 500)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            status = OrderStatus.PENDING
        )
        val discount = Discount(
            code = "BIGORDER",
            type = DiscountType.FIXED_AMOUNT,
            value = 1000,
            minOrderAmount = 10000
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { pricingService.validateDiscount("BIGORDER", order) } returns null

        // Act
        val result = orderService.applyDiscount("ORDER-1", "BIGORDER")

        // Assert
        assertThat(result.isFailure).isTrue()

        // TODO: Med mocks må vi sette opp nøyaktig hva som skal returneres
        verify { pricingService.validateDiscount("BIGORDER", any()) }
    }

    @Test
    fun `skal beregne total med rabatt korrekt`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 20000)
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1)),
            discountCode = "DISCOUNT10"
        )
        val discount = Discount(
            code = "DISCOUNT10",
            type = DiscountType.PERCENTAGE,
            value = 10
        )

        every { orderRepository.findById("ORDER-1") } returns order
        every { pricingService.validateDiscount("DISCOUNT10", order) } returns discount
        every { pricingService.calculateDiscountAmount("DISCOUNT10", 20000) } returns 2000

        // Act
        val result = orderService.calculateOrderTotal("ORDER-1")

        // Assert
        assertThat(result.isSuccess).isTrue()
        val total = result.getOrThrow()
        assertThat(total.subtotal).isEqualTo(20000)
        assertThat(total.discountAmount).isEqualTo(2000)
        assertThat(total.tax).isEqualTo(5000)
        assertThat(total.total).isEqualTo(23000)

        // TODO: Må verifisere alle beregninger - blir fort mange mock-interaksjoner
        verify { pricingService.calculateDiscountAmount("DISCOUNT10", 20000) }
    }

    @Test
    fun `skal hente alle ordrer for en kunde`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val order1 = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1))
        )
        val order2 = Order(
            id = "ORDER-2",
            customerId = "CUST-1",
            customerEmail = "test@test.com",
            items = listOf(OrderItem(product, quantity = 1))
        )

        every { orderRepository.findByCustomerId("CUST-1") } returns listOf(order1, order2)

        // Act
        val orders = orderService.getOrdersForCustomer("CUST-1")

        // Assert
        assertThat(orders).hasSize(2)
        assertThat(orders).containsExactly(order1, order2)

        // TODO: Med mocks må vi sette opp forventet returverdi nøyaktig
        verify { orderRepository.findByCustomerId("CUST-1") }
    }

    @Test
    @Disabled("Samtidighetsproblem")
    fun `skal håndtere flere ordre for samme produkt`() {
        // Arrange
        val product = Product(id = "PROD-1", name = "Product", price = 1000)
        val items1 = listOf(OrderItem(product, quantity = 10))
        val items2 = listOf(OrderItem(product, quantity = 15))

        // TODO: Med mocks blir dette veldig komplisert - må mocke hver operasjon
        every { inventoryService.isAvailable("PROD-1", 10) } returns true
        every { inventoryService.isAvailable("PROD-1", 15) } returns true
        every { inventoryService.reserve("PROD-1", 10) } just Runs
        every { inventoryService.reserve("PROD-1", 15) } just Runs
        every { inventoryService.confirm("PROD-1", 10) } just Runs
        every { inventoryService.release("PROD-1", 15) } just Runs
        every { orderRepository.save(any()) } returnsArgument 0
        every { orderRepository.findById(any()) } answers {
            val id = firstArg<String>()
            Order(
                id = id,
                customerId = "CUST-1",
                customerEmail = "test@test.com",
                items = items1,
                status = OrderStatus.PENDING
            )
        }
        every { notificationService.notifyOrderConfirmed(any()) } returns true
        every { notificationService.notifyOrderCancelled(any(), any()) } returns true

        // Act
        val order1 = orderService.createOrder("CUST-1", "cust1@example.com", items1).getOrThrow()
        val order2 = orderService.createOrder("CUST-2", "cust2@example.com", items2).getOrThrow()

        orderService.confirmOrder(order1.id)
        orderService.cancelOrder(order2.id, "Changed mind")

        // Assert
        // TODO: Med mocks kan vi ikke teste faktisk inventory-tilstand
        // Vi kan bare verifisere at metodene ble kalt riktig antall ganger
        verify(exactly = 1) { inventoryService.reserve("PROD-1", 10) }
        verify(exactly = 1) { inventoryService.reserve("PROD-1", 15) }
        verify(exactly = 1) { inventoryService.confirm("PROD-1", 10) }
        verify(exactly = 1) { inventoryService.release("PROD-1", 15) }
    }

    @Test
    fun `kompleks scenario - full ordre-flyt`() {
        // Arrange
        val laptop = Product(id = "LAPTOP-1", name = "MacBook Pro", price = 20000)
        val mouse = Product(id = "MOUSE-1", name = "Magic Mouse", price = 800)
        val items = listOf(
            OrderItem(laptop, quantity = 1),
            OrderItem(mouse, quantity = 3)
        )
        val discount = Discount(
            code = "WELCOME20",
            type = DiscountType.PERCENTAGE,
            value = 20,
            minOrderAmount = 5000
        )

        // TODO: Med mocks må vi sette opp ALLE interaksjoner - blir veldig stort!
        every { inventoryService.isAvailable("LAPTOP-1", 1) } returns true
        every { inventoryService.isAvailable("MOUSE-1", 3) } returns true
        every { inventoryService.reserve("LAPTOP-1", 1) } just Runs
        every { inventoryService.reserve("MOUSE-1", 3) } just Runs
        every { inventoryService.confirm("LAPTOP-1", 1) } just Runs
        every { inventoryService.confirm("MOUSE-1", 3) } just Runs
        every { orderRepository.save(any()) } returnsArgument 0
        every { orderRepository.findById(any()) } answers {
            Order(
                id = "ORDER-1",
                customerId = "CUST-1",
                customerEmail = "customer@example.com",
                items = items,
                discountCode = "WELCOME20",
                status = OrderStatus.PENDING
            )
        }
        every { pricingService.validateDiscount("WELCOME20", any()) } returns discount
        every { pricingService.calculateDiscountAmount("WELCOME20", 22400) } returns 4480
        every { notificationService.notifyOrderConfirmed(any()) } returns true

        // Act
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()
        orderService.applyDiscount(order.id, "WELCOME20")
        val total = orderService.calculateOrderTotal(order.id).getOrThrow()
        orderService.confirmOrder(order.id)

        // Assert
        assertThat(total.subtotal).isEqualTo(22400)
        assertThat(total.discountAmount).isEqualTo(4480)
        assertThat(total.tax).isEqualTo(5600)
        assertThat(total.total).isEqualTo(23520)

        // TODO: Må verifisere alle mock-interaksjoner - veldig mange!
        verify { inventoryService.reserve("LAPTOP-1", 1) }
        verify { inventoryService.reserve("MOUSE-1", 3) }
        verify { inventoryService.confirm("LAPTOP-1", 1) }
        verify { inventoryService.confirm("MOUSE-1", 3) }
        verify { pricingService.calculateDiscountAmount("WELCOME20", 22400) }
        verify { notificationService.notifyOrderConfirmed(any()) }

        // TODO: Med mocks kan vi IKKE teste faktisk tilstand av inventory
        // Vi kan bare verifisere at riktige metoder ble kalt
        // Sammenlign med OrderServiceTest som tester faktisk oppførsel!
    }
}