package services.inventory

data class InventoryItem(
    val productId: String,
    val availableQuantity: Int,
    val reservedQuantity: Int = 0
) {
    val totalQuantity: Int get() = availableQuantity + reservedQuantity
    
    fun reserve(quantity: Int): InventoryItem {
        require(availableQuantity >= quantity) { 
            "Insufficient inventory: available=$availableQuantity, requested=$quantity" 
        }
        return copy(
            availableQuantity = availableQuantity - quantity,
            reservedQuantity = reservedQuantity + quantity
        )
    }
    
    fun confirm(quantity: Int): InventoryItem {
        require(reservedQuantity >= quantity) { 
            "Insufficient reserved inventory: reserved=$reservedQuantity, requested=$quantity" 
        }
        return copy(reservedQuantity = reservedQuantity - quantity)
    }
    
    fun release(quantity: Int): InventoryItem {
        return copy(
            availableQuantity = availableQuantity + quantity,
            reservedQuantity = maxOf(0, reservedQuantity - quantity)
        )
    }
}