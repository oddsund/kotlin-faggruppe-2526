package domain

data class OrderItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Int
        get() = product.price * quantity
}