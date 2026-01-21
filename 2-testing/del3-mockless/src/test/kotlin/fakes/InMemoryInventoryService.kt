package fakes

import services.inventory.InventoryItem
import services.inventory.InventoryService

class InMemoryInventoryService : InventoryService {
    private val inventory = mutableMapOf<String, InventoryItem>()

    override fun isAvailable(productId: String, quantity: Int): Boolean {
        val item = inventory[productId] ?: return false
        return item.availableQuantity >= quantity
    }

    override fun reserve(productId: String, quantity: Int) {
        val item = inventory[productId]
            ?: throw IllegalStateException("Product not found: $productId")
        inventory[productId] = item.reserve(quantity)
    }

    override fun confirm(productId: String, quantity: Int) {
        val item = inventory[productId]
            ?: throw IllegalStateException("Product not found: $productId")
        inventory[productId] = item.confirm(quantity)
    }

    override fun release(productId: String, quantity: Int) {
        val item = inventory[productId]
            ?: throw IllegalStateException("Product not found: $productId")
        inventory[productId] = item.release(quantity)
    }

    override fun getInventory(productId: String): InventoryItem? {
        return inventory[productId]
    }

    override fun addInventory(productId: String, quantity: Int) {
        val existing = inventory[productId]
        inventory[productId] = if (existing != null) {
            existing.copy(availableQuantity = existing.availableQuantity + quantity)
        } else {
            InventoryItem(productId, quantity)
        }
    }

    fun clear() = inventory.clear()
}