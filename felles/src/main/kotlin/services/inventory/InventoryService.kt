package services.inventory

interface InventoryService {
    fun isAvailable(productId: String, quantity: Int): Boolean
    fun reserve(productId: String, quantity: Int)
    fun confirm(productId: String, quantity: Int)
    fun release(productId: String, quantity: Int)
    fun getInventory(productId: String): InventoryItem?
    fun addInventory(productId: String, quantity: Int)
}

