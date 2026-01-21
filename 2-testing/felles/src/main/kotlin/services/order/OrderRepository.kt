package services.order

import domain.Order

interface OrderRepository {
    fun findById(id: String): Order?
    fun save(order: Order): Order
    fun findByCustomerId(customerId: String): List<Order>
}


