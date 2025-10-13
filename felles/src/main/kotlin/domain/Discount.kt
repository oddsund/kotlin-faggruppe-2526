package domain

data class Discount(
    val code: String,
    val type: DiscountType,
    val value: Int, // percentage or fixed amount
    val minOrderAmount: Int = 0,
    val expiryDate: Long? = null,
    val usageLimit: Int? = null,
    val usageCount: Int = 0
) {
    fun isValid(): Boolean {
        if (expiryDate != null && System.currentTimeMillis() > expiryDate) {
            return false
        }
        if (usageLimit != null && usageCount >= usageLimit) {
            return false
        }
        return true
    }
    
    fun isApplicableTo(order: Order): Boolean {
        return order.subtotal >= minOrderAmount
    }
    
    fun calculateDiscount(subtotal: Int): Int {
        return when (type) {
            DiscountType.PERCENTAGE -> (subtotal * value) / 100
            DiscountType.FIXED_AMOUNT -> minOf(value, subtotal)
        }
    }
}