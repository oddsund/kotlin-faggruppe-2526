package integration

import assertk.assertThat
import assertk.assertions.*
import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import infra.DatabaseFactory
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
        // TODO: Verifiser at retrieved ikke er null
        // TODO: Verifiser at ordre-ID er korrekt
        // TODO: Verifiser at customerId er korrekt
        // TODO: Verifiser at customerEmail er korrekt
        // TODO: Verifiser at status er PENDING
        // TODO: Verifiser at items har riktig størrelse
        // TODO: Verifiser at første item har riktig produkt-ID og quantity
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
        // TODO: Verifiser at status er oppdatert til CONFIRMED
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
        // TODO: Verifiser at kunde1 har 3 ordrer
        // TODO: Verifiser at kunde2 har 2 ordrer
        // TODO: Verifiser at alle ordrer for kunde1 har riktig customerId
    }

    @Test
    fun `skal returnere null for ordre som ikke finnes`() {
        // Act
        val result = repository.findById("NON-EXISTENT")

        // Assert
        // TODO: Verifiser at resultatet er null
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
        // TODO: Verifiser at retrieved ikke er null
        // TODO: Verifiser at alle 10 items er lagret
        // TODO: Verifiser at hvert item har riktig produkt-ID og quantity
        // Tips: Bruk forEachIndexed eller lignende for å sjekke alle items
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
        // TODO: Verifiser at discountCode er bevart
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
        // TODO: Verifiser at discountCode er null
    }
}