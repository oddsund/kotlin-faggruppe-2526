package løsningsforslag

import domain.Order
import domain.OrderItem
import domain.Product
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertContains

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

        assertEquals(21600, order.subtotal)
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

        assertEquals(25000, order.total)
        assertEquals(5000, order.tax)
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

        assertEquals(2, order.items.size)
        assertTrue(order.items.isNotEmpty())
    }

    @Test
    fun `skal ikke ha rabattkode som standard`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        assertNull(order.discountCode)
        assertFalse(order.hasDiscount())
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

        assertNotNull(order.discountCode)
        assertEquals("SUMMER2025", order.discountCode)
        assertTrue(order.hasDiscount())
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

        assertContains(order.items, OrderItem(laptop, quantity = 1))

        assertTrue(order.containsProduct("LAPTOP-1"))
        assertTrue(order.containsProduct("MOUSE-1"))
        assertFalse(order.containsProduct("KEYBOARD-1"))
    }

    @Test
    fun `skal ha riktig kunde-id`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-123",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )

        assertEquals("CUST-123", order.customerId)
    }

    @Test
    fun `skal beregne element-subtotal korrekt`() {
        val item = OrderItem(mouse, quantity = 3)

        assertEquals(2400, item.subtotal)
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

        assertAll(
            { assertEquals("ORDER-1", order.id) },
            { assertEquals("CUST-1", order.customerId) },
            { assertNotNull(order.discountCode) }
        )
    }
}