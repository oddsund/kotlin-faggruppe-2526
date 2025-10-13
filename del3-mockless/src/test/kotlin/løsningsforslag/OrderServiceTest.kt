import assertk.assertThat
import assertk.assertions.*
import domain.DiscountType
import domain.Order
import domain.OrderStatus
import fakes.InMemoryInventoryService
import fakes.InMemoryOrderRepository
import io.mockk.mockk
import io.mockk.verify
import løsningsforslag.aDiscount
import løsningsforslag.aProduct
import løsningsforslag.anOrderItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import services.notification.NotificationService
import services.order.*
import services.pricing.InMemoryPricingService

class OrderServiceTest {

    private lateinit var orderRepository: InMemoryOrderRepository
    private lateinit var inventoryService: InMemoryInventoryService
    private lateinit var pricingService: InMemoryPricingService
    private lateinit var notificationService: NotificationService // Denne MÅ mockes (ekstern)
    private lateinit var orderService: OrderService

    private val laptop = aProduct(id = "LAPTOP-1", name = "MacBook Pro", price = 20000)
    private val mouse = aProduct(id = "MOUSE-1", name = "Magic Mouse", price = 800)

    @BeforeEach
    fun setup() {
        // Bruk in-memory implementasjoner i stedet for mocks
        orderRepository = InMemoryOrderRepository()
        inventoryService = InMemoryInventoryService()
        pricingService = InMemoryPricingService()

        // Kun NotificationService er mock (ekstern avhengighet)
        notificationService = mockk(relaxed = true)

        orderService = OrderService(
            orderRepository,
            inventoryService,
            pricingService,
            notificationService
        )

        // Setup inventory for test-produkter
        inventoryService.addInventory(laptop.id, 100)
        inventoryService.addInventory(mouse.id, 200)
    }

    @Test
    fun `skal opprette ordre når produkter er på lager`() {
        // Arrange
        val items = listOf(
            anOrderItem(laptop, quantity = 2),
            anOrderItem(mouse, quantity = 3)
        )

        // Act
        val result = orderService.createOrder(
            customerId = "CUST-1",
            customerEmail = "customer@example.com",
            items = items
        )

        // Assert - test oppførsel, ikke implementasjon
        assertThat(result.isSuccess).isTrue()

        val order = result.getOrThrow()
        assertThat(order.customerId).isEqualTo("CUST-1")
        assertThat(order.items).hasSize(2)
        assertThat(order.status).isEqualTo(OrderStatus.PENDING)

        // Verifiser at inventory ble reservert (oppførsel)
        val laptopInventory = inventoryService.getInventory(laptop.id)!!
        assertThat(laptopInventory.availableQuantity).isEqualTo(98)
        assertThat(laptopInventory.reservedQuantity).isEqualTo(2)

        val mouseInventory = inventoryService.getInventory(mouse.id)!!
        assertThat(mouseInventory.availableQuantity).isEqualTo(197)
        assertThat(mouseInventory.reservedQuantity).isEqualTo(3)
    }

    @Test
    fun `skal feile hvis produkter ikke er på lager`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 150)) // Mer enn på lager

        // Act
        val result = orderService.createOrder("CUST-1", "customer@example.com", items)

        // Assert - test oppførsel
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InsufficientInventoryException::class.java)

        // Verifiser at inventory IKKE ble endret (oppførsel)
        val laptopInventory = inventoryService.getInventory(laptop.id)!!
        assertThat(laptopInventory.availableQuantity).isEqualTo(100)
        assertThat(laptopInventory.reservedQuantity).isEqualTo(0)
    }

    @Test
    fun `skal bekrefte ordre og oppdatere status`() {
        // Arrange - opprett en ordre først
        val items = listOf(anOrderItem(laptop, quantity = 1))
        val createResult = orderService.createOrder("CUST-1", "customer@example.com", items)
        val order = createResult.getOrThrow()

        // Act
        val confirmResult = orderService.confirmOrder(order.id)

        // Assert - test oppførsel
        assertThat(confirmResult.isSuccess).isTrue()

        val confirmedOrder = confirmResult.getOrThrow()
        assertThat(confirmedOrder.status).isEqualTo(OrderStatus.CONFIRMED)

        // Verifiser at inventory-reservasjon ble bekreftet
        val laptopInventory = inventoryService.getInventory(laptop.id)!!
        assertThat(laptopInventory.availableQuantity).isEqualTo(99)
        assertThat(laptopInventory.reservedQuantity).isEqualTo(0) // Bekreftet, ikke lenger reservert

        // Verifiser at ordre er lagret med ny status
        val savedOrder = orderRepository.findById(order.id)
        assertThat(savedOrder?.status).isEqualTo(OrderStatus.CONFIRMED)

        // Kun ekstern tjeneste må verifiseres
        verify(exactly = 1) { notificationService.notifyOrderConfirmed(order.id) }
    }

    @Test
    fun `skal ikke kunne bekrefte ordre som ikke er PENDING`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 1))
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()
        orderService.confirmOrder(order.id) // Bekreft først

        // Act - prøv å bekrefte igjen
        val result = orderService.confirmOrder(order.id)

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InvalidOrderStateException::class.java)
    }

    @Test
    fun `skal kansellere ordre og frigi inventory`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 2))
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        val inventoryBeforeCancel = inventoryService.getInventory(laptop.id)!!
        assertThat(inventoryBeforeCancel.reservedQuantity).isEqualTo(2)

        // Act
        val result = orderService.cancelOrder(order.id, "Customer requested cancellation")

        // Assert
        assertThat(result.isSuccess).isTrue()

        val cancelledOrder = result.getOrThrow()
        assertThat(cancelledOrder.status).isEqualTo(OrderStatus.CANCELLED)

        // Verifiser at inventory ble frigitt
        val inventoryAfterCancel = inventoryService.getInventory(laptop.id)!!
        assertThat(inventoryAfterCancel.availableQuantity).isEqualTo(100)
        assertThat(inventoryAfterCancel.reservedQuantity).isEqualTo(0)
    }

    @Test
    fun `skal kunne legge til rabattkode på PENDING ordre`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 1))
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        val discount = aDiscount(
            code = "SUMMER2025",
            type = DiscountType.PERCENTAGE,
            value = 10,
            minOrderAmount = 1000
        )
        pricingService.addDiscount(discount)

        // Act
        val result = orderService.applyDiscount(order.id, "SUMMER2025")

        // Assert
        assertThat(result.isSuccess).isTrue()

        val discountedOrder = result.getOrThrow()
        assertThat(discountedOrder.discountCode).isEqualTo("SUMMER2025")
    }

    @Test
    fun `skal ikke kunne legge til ugyldig rabattkode`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 1))
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        // Act - prøv å bruke rabattkode som ikke finnes
        val result = orderService.applyDiscount(order.id, "INVALID")

        // Assert
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull() as Throwable).isInstanceOf(InvalidDiscountException::class.java)
    }

    @Test
    fun `skal ikke kunne legge til rabatt hvis ordre under minimum beløp`() {
        // Arrange
        val items = listOf(anOrderItem(mouse, quantity = 1)) // Billig produkt
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        val discount = aDiscount(
            code = "BIGORDER",
            type = DiscountType.FIXED_AMOUNT,
            value = 1000,
            minOrderAmount = 10000 // Krever høyere ordre-beløp
        )
        pricingService.addDiscount(discount)

        // Act
        val result = orderService.applyDiscount(order.id, "BIGORDER")

        // Assert
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `skal beregne total med rabatt korrekt`() {
        // Arrange
        val items = listOf(anOrderItem(laptop, quantity = 1)) // 20000 NOK
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        val discount = aDiscount(
            code = "DISCOUNT10",
            type = DiscountType.PERCENTAGE,
            value = 10
        )
        pricingService.addDiscount(discount)
        orderService.applyDiscount(order.id, "DISCOUNT10")

        // Act
        val totalResult = orderService.calculateOrderTotal(order.id)

        // Assert
        assertThat(totalResult.isSuccess).isTrue()

        val total = totalResult.getOrThrow()
        assertThat(total.subtotal).isEqualTo(20000)
        assertThat(total.discountAmount).isEqualTo(2000) // 10% av 20000
        assertThat(total.tax).isEqualTo(5000) // 25% av 20000
        assertThat(total.total).isEqualTo(23000) // 20000 - 2000 + 5000
    }

    @Test
    @Disabled("Får av en eller annen grunn tomt array for customer1")
    fun `skal hente alle ordrer for en kunde`() {
        // Arrange
        val customer1Items = listOf(anOrderItem(laptop, quantity = 1))
        val customer2Items = listOf(anOrderItem(mouse, quantity = 2))

        val res1 = orderService.createOrder("CUST-1", "cust1@example.com", customer1Items)
        val res2 = orderService.createOrder("CUST-1", "cust1@example.com", listOf(anOrderItem(mouse, quantity = 1)))
        val res3 = orderService.createOrder("CUST-2", "cust2@example.com", customer2Items)

        assertThat(res1.isSuccess).isTrue()
        assertThat(res2.isSuccess).isTrue()
        assertThat(res3.isSuccess).isTrue()

        // Act
        val customer1Orders = orderService.getOrdersForCustomer("CUST-1")
        val customer2Orders = orderService.getOrdersForCustomer("CUST-2")

        // Assert
        assertThat(customer1Orders).hasSize(2)
        assertThat(customer2Orders).hasSize(1)

        assertThat(customer1Orders).each {
            it.prop(Order::customerId).isEqualTo("CUST-1")
        }
    }

    @Test
    @Disabled("Noe samtidighetsproblem her")
    fun `skal håndtere flere ordre for samme produkt`() {
        // Arrange
        val items1 = listOf(anOrderItem(laptop, quantity = 10))
        val items2 = listOf(anOrderItem(laptop, quantity = 15))

        // Act
        val order1 = orderService.createOrder("CUST-1", "cust1@example.com", items1).getOrThrow()
        val order2 = orderService.createOrder("CUST-2", "cust2@example.com", items2).getOrThrow()

        // Assert - verifiser at inventory holder styr på begge reservasjonene
        val inventory = inventoryService.getInventory(laptop.id)!!
        assertThat(inventory.availableQuantity).isEqualTo(75) // 100 - 10 - 15
        assertThat(inventory.reservedQuantity).isEqualTo(25) // 10 + 15

        // Bekreft første ordre
        orderService.confirmOrder(order1.id)

        val inventoryAfterConfirm = inventoryService.getInventory(laptop.id)!!
        assertThat(inventoryAfterConfirm.availableQuantity).isEqualTo(75)
        assertThat(inventoryAfterConfirm.reservedQuantity).isEqualTo(15) // Bare order2 igjen

        // Kanseller andre ordre
        orderService.cancelOrder(order2.id, "Changed mind")

        val inventoryAfterCancel = inventoryService.getInventory(laptop.id)!!
        assertThat(inventoryAfterCancel.availableQuantity).isEqualTo(90) // 75 + 15
        assertThat(inventoryAfterCancel.reservedQuantity).isEqualTo(0)
    }

    @Test
    fun `kompleks scenario - full ordre-flyt`() {
        // Arrange - setup discount og inventory
        val discount = aDiscount(
            code = "WELCOME20",
            type = DiscountType.PERCENTAGE,
            value = 20,
            minOrderAmount = 5000
        )
        pricingService.addDiscount(discount)

        // Act - opprett ordre med flere produkter
        val items = listOf(
            anOrderItem(laptop, quantity = 1),
            anOrderItem(mouse, quantity = 3)
        )
        val order = orderService.createOrder("CUST-1", "customer@example.com", items).getOrThrow()

        // Legg til rabatt
        orderService.applyDiscount(order.id, "WELCOME20")

        // Beregn total
        val total = orderService.calculateOrderTotal(order.id).getOrThrow()

        // Bekreft ordre
        val confirmedOrder = orderService.confirmOrder(order.id).getOrThrow()

        // Assert - verifiser hele flyten
        assertThat(confirmedOrder.status).isEqualTo(OrderStatus.CONFIRMED)
        assertThat(confirmedOrder.discountCode).isEqualTo("WELCOME20")

        // Subtotal = 20000 + (800 * 3) = 22400
        assertThat(total.subtotal).isEqualTo(22400)
        // Discount = 20% av 22400 = 4480
        assertThat(total.discountAmount).isEqualTo(4480)
        // Tax = 25% av 22400 = 5600
        assertThat(total.tax).isEqualTo(5600)
        // Total = 22400 - 4480 + 5600 = 23520
        assertThat(total.total).isEqualTo(23520)

        // Verifiser inventory
        val laptopInv = inventoryService.getInventory(laptop.id)!!
        assertThat(laptopInv.availableQuantity).isEqualTo(99)
        assertThat(laptopInv.reservedQuantity).isEqualTo(0)

        val mouseInv = inventoryService.getInventory(mouse.id)!!
        assertThat(mouseInv.availableQuantity).isEqualTo(197)
        assertThat(mouseInv.reservedQuantity).isEqualTo(0)

        // Verifiser at notifikasjon ble sendt (ekstern avhengighet)
        verify(exactly = 1) { notificationService.notifyOrderConfirmed(order.id) }
    }
}