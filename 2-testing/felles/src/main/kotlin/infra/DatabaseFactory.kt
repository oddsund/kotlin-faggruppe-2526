package infra

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    
    fun init(
        url: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        driver: String = "org.h2.Driver",
        user: String = "sa",
        password: String = ""
    ) {
        val database = Database.connect(url, driver, user, password)
        
        transaction(database) {
            SchemaUtils.create(
                Orders,
                OrderItems,
                Inventory,
                Discounts
            )
        }
    }
    
    fun reset() {
        transaction {
            SchemaUtils.drop(
                Orders,
                OrderItems,
                Inventory,
                Discounts
            )
            SchemaUtils.create(
                Orders,
                OrderItems,
                Inventory,
                Discounts
            )
        }
    }
}

object Orders : Table("orders") {
    val id = varchar("id", 50)
    val customerId = varchar("customer_id", 50)
    val customerEmail = varchar("customer_email", 255)
    val status = varchar("status", 20)
    val discountCode = varchar("discount_code", 50).nullable()
    val taxRate = double("tax_rate")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    
    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = varchar("order_id", 50).references(Orders.id)
    val productId = varchar("product_id", 50)
    val productName = varchar("product_name", 255)
    val productPrice = integer("product_price")
    val quantity = integer("quantity")
    
    override val primaryKey = PrimaryKey(id)
}

object Inventory : Table("inventory") {
    val productId = varchar("product_id", 50)
    val availableQuantity = integer("available_quantity")
    val reservedQuantity = integer("reserved_quantity")
    val updatedAt = long("updated_at")
    
    override val primaryKey = PrimaryKey(productId)
}

object Discounts : Table("discounts") {
    val code = varchar("code", 50)
    val type = varchar("type", 20)
    val value = integer("value")
    val minOrderAmount = integer("min_order_amount")
    val expiryDate = long("expiry_date").nullable()
    val usageLimit = integer("usage_limit").nullable()
    val usageCount = integer("usage_count")
    
    override val primaryKey = PrimaryKey(code)
}