package løsningsforslag

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAtLeast
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import domain.Order
import domain.OrderItem
import domain.Product
import org.junit.jupiter.api.Test

// Dokumentasjon: https://github.com/assertk-org/assertk#usage
class OrderTestAssertK {

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

        assertThat(order.subtotal).isEqualTo(21600)
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

        assertThat(order.total).isEqualTo(25000)
        assertThat(order.tax).isEqualTo(5000)
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

        assertThat(order.items).hasSize(2)
        assertThat(order.items).isNotEmpty()
    }

    @Test
    fun `skal ikke ha rabattkode som standard`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        assertThat(order.discountCode).isNull()
        assertThat(order.hasDiscount()).isFalse()
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

        assertThat(order.discountCode)
            .isNotNull()
            .isEqualTo("SUMMER2025")
        assertThat(order.hasDiscount()).isTrue()
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

        assertThat(order.items).contains(OrderItem(laptop, quantity = 1))
        assertThat(order.items).containsAtLeast(OrderItem(mouse, quantity = 2), OrderItem(laptop, quantity = 1))

        assertThat(order.containsProduct("LAPTOP-1")).isTrue()
        assertThat(order.containsProduct("MOUSE-1")).isTrue()
        assertThat(order.containsProduct("KEYBOARD-1")).isFalse()
    }

    @Test
    fun `skal ha riktig kunde-id`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-123",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        assertThat(order.customerId).isEqualTo("CUST-123")
    }

    @Test
    fun `skal beregne element-subtotal korrekt`() {
        val item = OrderItem(mouse, quantity = 3)

        assertThat(item.subtotal).isEqualTo(2400)
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

        // Kan også gjøres med apply for ikke-soft
        assertThat(order).all {
            prop(Order::id).isEqualTo("ORDER-1")
            prop(Order::customerId).isEqualTo("CUST-1")
            prop(Order::discountCode).isNotNull()
        }
    }
}