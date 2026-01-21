package integration.løsningsforslag

import assertk.assertThat
import assertk.assertions.*
import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import infra.DatabaseFactory
import integration.aProduct
import integration.anOrder
import integration.anOrderItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.order.JdbcOrderRepository
import services.order.OrderRepository

// H2 versjon - raskere, men ikke identisk med prod
class OrderRepositoryIntegrationTestWithH2 {

    private lateinit var repository: OrderRepository

    private val laptop = aProduct(id = "LAPTOP-1", name = "MacBook Pro", price = 20000)
    private val mouse = aProduct(id = "MOUSE-1", name = "Magic Mouse", price = 800)

    @BeforeEach
    fun setup() {
        DatabaseFactory.init() // Bruker H2 in-memory som default
        repository = JdbcOrderRepository()
        DatabaseFactory.reset()
    }

    @Test
    fun `skal lagre og hente ordre fra database`() {
        // Arrange
        val order = anOrder(
            id = "ORDER-123",
            customerId = "CUST-1",
            customerEmail = "customer@example.com",
            items = listOf(
                anOrderItem(laptop, quantity = 1),
                anOrderItem(mouse, quantity = 2)
            ),
            status = OrderStatus.PENDING
        )

        // Act
        repository.save(order)
        val retrieved = repository.findById(order.id)

        // Assert
        assertThat(retrieved).isNotNull()
        assertThat(retrieved!!).apply {
            prop(Order::id).isEqualTo("ORDER-123")
            prop(Order::customerId).isEqualTo("CUST-1")
            prop(Order::customerEmail).isEqualTo("customer@example.com")
            prop(Order::status).isEqualTo(OrderStatus.PENDING)
            prop(Order::items).hasSize(2)
        }

        // Verifiser items
        assertThat(retrieved.items[0]).apply {
            prop(OrderItem::product).prop(Product::id).isEqualTo("LAPTOP-1")
            prop(OrderItem::quantity).isEqualTo(1)
        }
    }

    @Test
    fun `skal oppdatere eksisterende ordre`() {
        // Arrange
        val order = anOrder(id = "ORDER-123", status = OrderStatus.PENDING)
        repository.save(order)

        // Act
        val updatedOrder = order.confirm()
        repository.save(updatedOrder)

        // Assert
        val retrieved = repository.findById("ORDER-123")
        assertThat(retrieved?.status).isEqualTo(OrderStatus.CONFIRMED)
    }

    @Test
    fun `skal hente alle ordrer for en kunde`() {
        // Arrange
        repository.save(anOrder(id = "ORDER-1", customerId = "CUST-1"))
        repository.save(anOrder(id = "ORDER-2", customerId = "CUST-1"))
        repository.save(anOrder(id = "ORDER-3", customerId = "CUST-1"))
        repository.save(anOrder(id = "ORDER-4", customerId = "CUST-2"))
        repository.save(anOrder(id = "ORDER-5", customerId = "CUST-2"))

        // Act
        val customer1Orders = repository.findByCustomerId("CUST-1")
        val customer2Orders = repository.findByCustomerId("CUST-2")

        // Assert
        assertThat(customer1Orders).hasSize(3)
        assertThat(customer2Orders).hasSize(2)

        assertThat(customer1Orders).each {
            it.prop(Order::customerId).isEqualTo("CUST-1")
        }
    }

    @Test
    fun `skal returnere null for ordre som ikke finnes`() {
        // Act
        val result = repository.findById("NON-EXISTENT")

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `skal håndtere ordre med mange items`() {
        // Arrange
        val items = (1..10).map { i ->
            anOrderItem(
                product = aProduct(id = "PROD-$i", name = "Product $i", price = i * 100),
                quantity = i
            )
        }
        val order = anOrder(id = "ORDER-MANY", items = items)

        // Act
        repository.save(order)
        val retrieved = repository.findById("ORDER-MANY")

        // Assert
        assertThat(retrieved).isNotNull()
        assertThat(retrieved!!.items).hasSize(10)

        retrieved.items.forEachIndexed { index, item ->
            val expected = index + 1
            assertThat(item.product.id).isEqualTo("PROD-$expected")
            assertThat(item.quantity).isEqualTo(expected)
        }
    }

    @Test
    fun `skal bevare discount code ved lagring`() {
        // Arrange
        val order = anOrder(
            id = "ORDER-DISCOUNT",
            discountCode = "SUMMER2025"
        )

        // Act
        repository.save(order)
        val retrieved = repository.findById("ORDER-DISCOUNT")

        // Assert
        assertThat(retrieved?.discountCode).isEqualTo("SUMMER2025")
    }

    @Test
    fun `skal håndtere null discount code`() {
        // Arrange
        val order = anOrder(
            id = "ORDER-NO-DISCOUNT",
            discountCode = null
        )

        // Act
        repository.save(order)
        val retrieved = repository.findById("ORDER-NO-DISCOUNT")

        // Assert
        assertThat(retrieved?.discountCode).isNull()
    }
}