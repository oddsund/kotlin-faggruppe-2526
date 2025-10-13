package services.order

import domain.Order
import domain.OrderItem
import domain.OrderStatus
import services.inventory.InventoryService
import services.notification.NotificationService
import services.pricing.PricingService

class OrderService(
    private val orderRepository: OrderRepository,
    private val inventoryService: InventoryService,
    private val pricingService: PricingService,
    private val notificationService: NotificationService
) {
    fun createOrder(customerId: String, customerEmail: String, items: List<OrderItem>): Result<Order> {
        // Valider at alle produkter er på lager
        val unavailableProducts = items.filter { item ->
            !inventoryService.isAvailable(item.product.id, item.quantity)
        }
        
        if (unavailableProducts.isNotEmpty()) {
            return Result.failure(
                InsufficientInventoryException(
                    "Products not available: ${unavailableProducts.map { it.product.id }}"
                )
            )
        }
        
        // Reserver inventory
        items.forEach { item ->
            inventoryService.reserve(item.product.id, item.quantity)
        }
        
        val order = Order(
            id = generateOrderId(),
            customerId = customerId,
            customerEmail = customerEmail,
            items = items,
            status = OrderStatus.PENDING
        )
        
        return Result.success(orderRepository.save(order))
    }
    
    fun confirmOrder(orderId: String): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(OrderNotFoundException("Order not found: $orderId"))
        
        if (order.status != OrderStatus.PENDING) {
            return Result.failure(
                InvalidOrderStateException("Order must be in PENDING state, was: ${order.status}")
            )
        }
        
        // Bekreft inventory-reservasjon
        order.items.forEach { item ->
            inventoryService.confirm(item.product.id, item.quantity)
        }
        
        val confirmedOrder = order.confirm()
        orderRepository.save(confirmedOrder)
        
        // Send notifikasjon (ekstern operasjon)
        notificationService.notifyOrderConfirmed(orderId)
        
        return Result.success(confirmedOrder)
    }
    
    fun cancelOrder(orderId: String, reason: String): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(OrderNotFoundException("Order not found: $orderId"))
        
        if (order.status == OrderStatus.CANCELLED) {
            return Result.failure(
                InvalidOrderStateException("Order is already cancelled")
            )
        }
        
        // Frigi inventory
        order.items.forEach { item ->
            inventoryService.release(item.product.id, item.quantity)
        }
        
        val cancelledOrder = order.cancel()
        orderRepository.save(cancelledOrder)
        
        // Send notifikasjon (ekstern operasjon)
        notificationService.notifyOrderCancelled(orderId, reason)
        
        return Result.success(cancelledOrder)
    }
    
    fun applyDiscount(orderId: String, discountCode: String): Result<Order> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(OrderNotFoundException("Order not found: $orderId"))
        
        if (order.status != OrderStatus.PENDING) {
            return Result.failure(
                InvalidOrderStateException("Cannot apply discount to order in ${order.status} state")
            )
        }
        
        val discount = pricingService.validateDiscount(discountCode, order)
            ?: return Result.failure(InvalidDiscountException("Invalid or expired discount code: $discountCode"))
        
        val discountedOrder = order.copy(discountCode = discountCode)
        return Result.success(orderRepository.save(discountedOrder))
    }
    
    fun getOrdersForCustomer(customerId: String): List<Order> {
        return orderRepository.findByCustomerId(customerId)
    }
    
    fun calculateOrderTotal(orderId: String): Result<OrderTotal> {
        val order = orderRepository.findById(orderId)
            ?: return Result.failure(OrderNotFoundException("Order not found: $orderId"))
        
        val subtotal = order.subtotal
        val discountAmount = if (order.discountCode != null) {
            pricingService.calculateDiscountAmount(order.discountCode, subtotal)
        } else {
            0
        }
        
        val subtotalAfterDiscount = subtotal - discountAmount
        val tax = order.tax
        val total = subtotalAfterDiscount + tax
        
        return Result.success(
            OrderTotal(
                subtotal = subtotal,
                discountAmount = discountAmount,
                tax = tax,
                total = total
            )
        )
    }
    
    private fun generateOrderId(): String {
        return "ORDER-${System.currentTimeMillis()}"
    }
}

data class OrderTotal(
    val subtotal: Int,
    val discountAmount: Int,
    val tax: Int,
    val total: Int
)

class OrderNotFoundException(message: String) : Exception(message)
class InvalidOrderStateException(message: String) : Exception(message)
class InsufficientInventoryException(message: String) : Exception(message)
class InvalidDiscountException(message: String) : Exception(message)