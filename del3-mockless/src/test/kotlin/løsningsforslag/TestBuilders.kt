package løsningsforslag

import domain.Discount
import domain.DiscountType
import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import services.inventory.InventoryItem

// Test builders for å gjøre testdata-oppretting enklere

fun aProduct(
    id: String = "PROD-${System.currentTimeMillis()}",
    name: String = "Test Product",
    price: Int = 100
) = Product(id, name, price)

fun anOrderItem(
    product: Product = aProduct(),
    quantity: Int = 1
) = OrderItem(product, quantity)

fun anOrder(
    id: String = "ORDER-${System.currentTimeMillis()}",
    customerId: String = "CUST-1",
    customerEmail: String = "customer@example.com",
    items: List<OrderItem> = listOf(anOrderItem()),
    status: OrderStatus = OrderStatus.PENDING,
    discountCode: String? = null,
    taxRate: Double = 0.25
) = Order(id, customerId, items, discountCode, taxRate, status, customerEmail)

fun aDiscount(
    code: String = "DISCOUNT10",
    type: DiscountType = DiscountType.PERCENTAGE,
    value: Int = 10,
    minOrderAmount: Int = 0,
    expiryDate: Long? = null,
    usageLimit: Int? = null
) = Discount(code, type, value, minOrderAmount, expiryDate, usageLimit)

fun anInventoryItem(
    productId: String = "PROD-1",
    availableQuantity: Int = 100,
    reservedQuantity: Int = 0
) = InventoryItem(productId, availableQuantity, reservedQuantity)