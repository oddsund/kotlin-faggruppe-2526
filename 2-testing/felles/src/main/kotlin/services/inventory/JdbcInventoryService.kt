package services.inventory

import infra.Inventory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class JdbcInventoryService : InventoryService {
    
    override fun isAvailable(productId: String, quantity: Int): Boolean = transaction {
        Inventory.select { Inventory.productId eq productId }
            .singleOrNull()
            ?.let { it[Inventory.availableQuantity] >= quantity }
            ?: false
    }
    
    override fun reserve(productId: String, quantity: Int) {
        transaction {
            val current = getInventoryRow(productId)
                ?: throw IllegalStateException("Product not found: $productId")
            
            if (current[Inventory.availableQuantity] < quantity) {
                throw IllegalStateException(
                    "Insufficient inventory for $productId: " +
                    "available=${current[Inventory.availableQuantity]}, requested=$quantity"
                )
            }
            
            Inventory.update({ Inventory.productId eq productId }) {
                it[availableQuantity] = current[Inventory.availableQuantity] - quantity
                it[reservedQuantity] = current[Inventory.reservedQuantity] + quantity
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }
    
    override fun confirm(productId: String, quantity: Int) {
        transaction {
            val current = getInventoryRow(productId)
                ?: throw IllegalStateException("Product not found: $productId")
            
            Inventory.update({ Inventory.productId eq productId }) {
                it[reservedQuantity] = current[Inventory.reservedQuantity] - quantity
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }
    
    override fun release(productId: String, quantity: Int) {
        transaction {
            val current = getInventoryRow(productId)
                ?: throw IllegalStateException("Product not found: $productId")
            
            Inventory.update({ Inventory.productId eq productId }) {
                it[availableQuantity] = current[Inventory.availableQuantity] + quantity
                it[reservedQuantity] = maxOf(0, current[Inventory.reservedQuantity] - quantity)
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }
    
    override fun getInventory(productId: String): InventoryItem? = transaction {
        getInventoryRow(productId)?.let { row ->
            InventoryItem(
                productId = row[Inventory.productId],
                availableQuantity = row[Inventory.availableQuantity],
                reservedQuantity = row[Inventory.reservedQuantity]
            )
        }
    }
    
    override fun addInventory(productId: String, quantity: Int) {
        transaction {
            val existing = getInventoryRow(productId)
            
            if (existing != null) {
                Inventory.update({ Inventory.productId eq productId }) {
                    it[availableQuantity] = existing[Inventory.availableQuantity] + quantity
                    it[updatedAt] = System.currentTimeMillis()
                }
            } else {
                Inventory.insert {
                    it[Inventory.productId] = productId
                    it[availableQuantity] = quantity
                    it[reservedQuantity] = 0
                    it[updatedAt] = System.currentTimeMillis()
                }
            }
        }
    }
    
    private fun getInventoryRow(productId: String): ResultRow? {
        return Inventory.select { Inventory.productId eq productId }
            .singleOrNull()
    }
}