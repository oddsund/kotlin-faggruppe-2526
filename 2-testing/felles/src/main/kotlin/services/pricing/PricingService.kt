package services.pricing

import domain.Discount
import domain.Order

interface PricingService {
    fun validateDiscount(code: String, order: Order): Discount?
    fun calculateDiscountAmount(code: String, subtotal: Int): Int
    fun addDiscount(discount: Discount)
}

class InMemoryPricingService : PricingService {
    private val discounts = mutableMapOf<String, Discount>()

    override fun validateDiscount(code: String, order: Order): Discount? {
        val discount = discounts[code] ?: return null

        if (!discount.isValid()) return null
        if (!discount.isApplicableTo(order)) return null

        return discount
    }

    override fun calculateDiscountAmount(code: String, subtotal: Int): Int {
        val discount = discounts[code] ?: return 0
        return discount.calculateDiscount(subtotal)
    }

    override fun addDiscount(discount: Discount) {
        discounts[discount.code] = discount
    }

    fun incrementUsage(code: String) {
        val discount = discounts[code] ?: return
        discounts[code] = discount.copy(usageCount = discount.usageCount + 1)
    }

    fun clear() = discounts.clear()
}