import domain.Order
import domain.OrderItem
import domain.Product
import org.junit.jupiter.api.Test

/*
 * JUnit 5 Assertions
 *
 * Viktige assertions du vil trenge:
 * - assertEquals(forventet, faktisk): Sammenligner to verdier for likhet
 * - assertTrue(betingelse): Sjekker at en betingelse er sann
 * - assertFalse(betingelse): Sjekker at en betingelse er usann
 * - assertNull(verdi): Sjekker at en verdi er null
 * - assertNotNull(verdi): Sjekker at en verdi ikke er null
 * - assertContains(collection, element): Sjekker at en collection inneholder et element
 * - assertAll({ assertion1 }, { assertion2 }, ...): Grupperer flere assertions som alle evalueres
 */
class OrderTestJUnit {

    private val laptop = Product(
        id = "LAPTOP-1",
        name = "MacBook Pro",
        price = 20000
    )

    private val mouse = Product(
        id = "MOUSE-1",
        name = "Magic Mouse",
        price = 800
    )

    @Test
    fun `skal beregne subtotal korrekt`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(
                OrderItem(laptop, quantity = 1),
                OrderItem(mouse, quantity = 2)
            ),
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at subtotal har riktig verdi
    }

    @Test
    fun `skal beregne total med avgift`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(
                OrderItem(laptop, quantity = 1)
            ),
            taxRate = 0.25,
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at total og tax har riktige verdier
    }

    @Test
    fun `skal ha flere elementer`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(
                OrderItem(laptop, quantity = 1),
                OrderItem(mouse, quantity = 2)
            ),
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk antall elementer og at listen ikke er tom
    }

    @Test
    fun `skal ikke ha rabattkode som standard`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at discountCode er null og at hasDiscount() returnerer false
    }

    @Test
    fun `skal ha rabattkode når oppgitt`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            discountCode = "SUMMER2025",
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at discountCode ikke er null, har riktig verdi, og at hasDiscount() er true
    }

    @Test
    fun `skal finne produkt i ordre`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(
                OrderItem(laptop, quantity = 1),
                OrderItem(mouse, quantity = 2)
            ),
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at items inneholder spesifikke produkter

        // TODO: Sjekk at containsProduct() returnerer riktige verdier
    }

    @Test
    fun `skal ha riktig kunde-id`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-123",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk at customerId har riktig verdi
    }

    @Test
    fun `skal beregne element-subtotal korrekt`() {
        val item = OrderItem(mouse, quantity = 3)

        // TODO: Sjekk at subtotal er korrekt beregnet
    }

    @Test
    fun `soft assertions - alle evalueres i forventningsgruppe`() {
        // JUnit har ikke innebygd soft assertions, men kan bruke assertAll
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            discountCode = "SUMMER2025",
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk flere verdier samtidig - alle assertions skal evalueres selv om en feiler
    }
}