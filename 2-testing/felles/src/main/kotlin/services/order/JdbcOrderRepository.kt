package services.order

import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import infra.OrderItems
import infra.Orders
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class JdbcOrderRepository : OrderRepository {
    
    override fun findById(id: String): Order? = transaction {
        Orders.select { Orders.id eq id }
            .map { toOrder(it) }
            .singleOrNull()
    }
    
    override fun save(order: Order): Order = transaction {
        val now = System.currentTimeMillis()
        
        // Upsert order
        val exists = Orders.select { Orders.id eq order.id }.count() > 0
        
        if (exists) {
            Orders.update({ Orders.id eq order.id }) {
                it[customerId] = order.customerId
                it[customerEmail] = order.customerEmail
                it[status] = order.status.name
                it[discountCode] = order.discountCode
                it[taxRate] = order.taxRate
                it[updatedAt] = now
            }
            
            // Slett gamle items
            OrderItems.deleteWhere { orderId eq order.id }
        } else {
            Orders.insert {
                it[id] = order.id
                it[customerId] = order.customerId
                it[customerEmail] = order.customerEmail
                it[status] = order.status.name
                it[discountCode] = order.discountCode
                it[taxRate] = order.taxRate
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
        
        // Insert items
        order.items.forEach { item ->
            OrderItems.insert {
                it[orderId] = order.id
                it[productId] = item.product.id
                it[productName] = item.product.name
                it[productPrice] = item.product.price
                it[quantity] = item.quantity
            }
        }
        
        order
    }
    
    override fun findByCustomerId(customerId: String): List<Order> = transaction {
        Orders.select { Orders.customerId eq customerId }
            .map { toOrder(it) }
    }
    
    private fun toOrder(row: ResultRow): Order {
        val orderId = row[Orders.id]
        val items = OrderItems.select { OrderItems.orderId eq orderId }
            .map { itemRow ->
                OrderItem(
                    product = Product(
                        id = itemRow[OrderItems.productId],
                        name = itemRow[OrderItems.productName],
                        price = itemRow[OrderItems.productPrice]
                    ),
                    quantity = itemRow[OrderItems.quantity]
                )
            }
        
        return Order(
            id = orderId,
            customerId = row[Orders.customerId],
            items = items,
            discountCode = row[Orders.discountCode],
            taxRate = row[Orders.taxRate],
            status = OrderStatus.valueOf(row[Orders.status]),
            customerEmail = row[Orders.customerEmail]
        )
    }
}