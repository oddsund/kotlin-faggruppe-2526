package domain
data class Order(
    val id: String,
    val customerId: String,
    val items: List<OrderItem>,
    val discountCode: String? = null,
    val taxRate: Double = 0.25,
    val status: OrderStatus = OrderStatus.PENDING,
    val customerEmail: String
) {
    val subtotal: Int
        get() = items.sumOf { it.subtotal }
    
    val tax: Int
        get() = (subtotal * taxRate).toInt()
    
    val total: Int
        get() = subtotal + tax
    
    fun hasDiscount(): Boolean = discountCode != null
    
    fun containsProduct(productId: String): Boolean =
        items.any { it.product.id == productId }

    fun confirm(): Order = copy(status = OrderStatus.CONFIRMED)
    fun cancel(): Order = copy(status = OrderStatus.CANCELLED)
    fun ship(): Order = copy(status = OrderStatus.SHIPPED)
}