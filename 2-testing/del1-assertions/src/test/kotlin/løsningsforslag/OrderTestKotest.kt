package løsningsforslag

import domain.Order
import domain.OrderItem
import domain.Product
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

// Dokumentasjon: https://kotest.io/docs/assertions/assertions.html
class OrderTestKotest {
    
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
        
        order.subtotal shouldBe 21600
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
        
        order.total shouldBe 25000
        order.tax shouldBe 5000
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
        
        order.items shouldHaveSize 2
        order.items.shouldNotBeEmpty()
    }
    
    @Test
    fun `skal ikke ha rabattkode som standard`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-1",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )
        
        order.discountCode.shouldBeNull()
        order.hasDiscount().shouldBeFalse()
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
        
        order.discountCode.shouldNotBeNull()
        order.discountCode shouldBe "SUMMER2025"
        order.hasDiscount().shouldBeTrue()
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

        order.items shouldContain OrderItem(laptop, quantity = 1)
        order.items shouldContainAll listOf(OrderItem(mouse, quantity = 2), OrderItem(laptop, quantity = 1))

        order.containsProduct("LAPTOP-1").shouldBeTrue()
        order.containsProduct("MOUSE-1").shouldBeTrue()
        order.containsProduct("KEYBOARD-1").shouldBeFalse()
    }
    
    @Test
    fun `skal ha riktig kunde-id`() {
        val order = Order(
            id = "ORDER-1",
            customerId = "CUST-123",
            items = listOf(OrderItem(laptop, quantity = 1)),
            customerEmail = "test@test.no"
        )
        
        order.customerId shouldBe "CUST-123"
    }
    
    @Test
    fun `skal beregne element-subtotal korrekt`() {
        val item = OrderItem(mouse, quantity = 3)
        
        item.subtotal shouldBe 2400
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

        assertSoftly(order) {
            id shouldBe "ORDER-1"
            customerId shouldBe "CUST-1"
            items shouldHaveSize 1
            discountCode.shouldNotBeNull()
            total shouldBeGreaterThan 0
        }
    }
}