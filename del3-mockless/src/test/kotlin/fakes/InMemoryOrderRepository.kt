package fakes

import domain.Order
import services.order.OrderRepository

class InMemoryOrderRepository : OrderRepository {
    private val orders = mutableMapOf<String, Order>()
    private val ordersByCustomerId = mutableMapOf<String, MutableList<String>>()

    override fun findById(id: String): Order? = orders[id]

    override fun save(order: Order): Order {
        // Fjern fra gammel kunde-indeks hvis ordre finnes fra før
        orders[order.id]?.let { existingOrder ->
            if (existingOrder.customerId != order.customerId) {
                ordersByCustomerId[existingOrder.customerId]?.remove(order.id)
            }
        }

        // Lagre ordre
        orders[order.id] = order

        // Oppdater kunde-indeks
        ordersByCustomerId
            .getOrPut(order.customerId) { mutableListOf() }
            .apply {
                if (!contains(order.id)) {
                    add(order.id)
                }
            }

        return order
    }

    override fun findByCustomerId(customerId: String): List<Order> =
        ordersByCustomerId[customerId]
            ?.mapNotNull { orderId -> orders[orderId] }
            ?: emptyList()

    fun clear() {
        orders.clear()
        ordersByCustomerId.clear()
    }
}