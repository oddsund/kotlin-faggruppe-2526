package løsningsforslag

import ch.tutteli.atrium.api.fluent.en_GB.feature
import ch.tutteli.atrium.api.fluent.en_GB.notToBeEmpty
import ch.tutteli.atrium.api.fluent.en_GB.notToEqualNull
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toHaveSize
import ch.tutteli.atrium.api.verbs.expect
import domain.Order
import domain.OrderItem
import domain.Product
import org.junit.jupiter.api.Test

// Dokumentasjon: https://github.com/robstoll/atrium#examples
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
        
        expect(order.subtotal).toEqual(21600)
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
        
        expect(order.total).toEqual(25000)
        expect(order.tax).toEqual(5000)
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
        
        expect(order.items).notToBeEmpty().toHaveSize(2)
    }
    
    @Test
    fun `skal ikke ha rabattkode som standard`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )
        
        expect(order.discountCode).toEqual(null)
        expect(order.hasDiscount()).toEqual(false)
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
        
        expect(order.discountCode).notToEqualNull().toEqual("SUMMER2025")
        expect(order.hasDiscount()).toEqual(true)
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

        expect(order.items).toContain(OrderItem(laptop, quantity = 1))
        expect(order.items).toContain(OrderItem(mouse, quantity = 2), OrderItem(laptop, quantity = 1))

        expect(order.containsProduct("LAPTOP-1")).toEqual(true)
        expect(order.containsProduct("MOUSE-1")).toEqual(true)
        expect(order.containsProduct("KEYBOARD-1")).toEqual(false)
    }
    
    @Test
    fun `skal ha riktig kunde-id`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-123",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )
        
        expect(order.customerId).toEqual("CUST-123")
    }
    
    @Test
    fun `skal beregne element-subtotal korrekt`() {
        val item = OrderItem(mouse, quantity = 3)
        
        expect(item.subtotal).toEqual(2400)
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
        
        expect(order) {
            feature { f(it::id) }.toEqual("ORDER-1")
            feature { f(it::customerId) }.toEqual("CUST-1")
            feature { f(it::discountCode) }.notToEqualNull()
        }
    }
}