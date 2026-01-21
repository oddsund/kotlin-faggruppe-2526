import domain.Order
import domain.OrderItem
import domain.Product
import org.junit.jupiter.api.Test

/*
 * Atrium - Assertion-bibliotek for Kotlin
 * Dokumentasjon: https://github.com/robstoll/atrium#examples
 *
 * Viktige assertions du vil trenge:
 * - expect(verdi).toEqual(forventet): Sammenligner verdier for likhet
 * - expect(verdi).toHaveSize(antall): Sjekker størrelsen på en collection
 * - expect(verdi).notToBeEmpty(): Sjekker at en collection ikke er tom
 * - expect(verdi).notToEqualNull(): Sjekker at en verdi ikke er null (kan knyttes til andre assertions)
 * - expect(verdi).toContain(element1, element2, ...): Sjekker at en collection inneholder elementer
 * - expect(objekt) { feature { f(it::property) }.assertion }: Soft assertions på properties
 */
class OrderTestAtrium {

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

        // TODO: Sjekk at discountCode er null og hasDiscount() returnerer false
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

        // TODO: Sjekk at discountCode ikke er null, har riktig verdi, og hasDiscount() returnerer true
        // Tips: Flere assertions kan settes sammen i en chain
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
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            discountCode = "SUMMER2025",
            customerEmail = "test@test.no"
        )

        // TODO: Sjekk flere properties på order samtidig - alle assertions evalueres selv om en feiler
    }
}