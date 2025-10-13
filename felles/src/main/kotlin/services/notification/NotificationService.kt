package services.notification

import services.email.BatchResult
import services.email.Email
import services.email.EmailService
import domain.Order
import domain.OrderStatus
import services.Logger
import services.order.OrderRepository

class NotificationService(
    private val emailService: EmailService,
    private val orderRepository: OrderRepository,
    private val logger: Logger
) {
    fun notifyOrderConfirmed(orderId: String): Boolean {
        logger.info("Processing order confirmation notification for order {}", orderId)

        val order = orderRepository.findById(orderId)
        if (order == null) {
            logger.error("Order not found: {}", null, orderId)
            return false
        }

        if (order.status != OrderStatus.PENDING) {
            logger.warn("Order {} is not in PENDING status, current status: {}", orderId, order.status)
            return false
        }

        return try {
            val email = createOrderConfirmationEmail(order)
            val sent = emailService.send(email)

            if (sent) {
                val confirmedOrder = order.confirm()
                orderRepository.save(confirmedOrder)
                logger.info("Order {} confirmed and notification sent to {}", orderId, order.customerEmail)
            } else {
                logger.error("Failed to send confirmation email for order {}", null, orderId)
            }

            sent
        } catch (e: Exception) {
            logger.error("Error sending order confirmation for order {}", e, orderId)
            false
        }
    }

    fun notifyOrderCancelled(orderId: String, reason: String): Boolean {
        logger.info("Processing order cancellation notification for order {}", orderId)

        val order = orderRepository.findById(orderId)
        if (order == null) {
            logger.error("Order not found: {}", null, orderId)
            return false
        }

        return try {
            val email = createOrderCancellationEmail(order, reason)
            val sent = emailService.send(email)

            if (sent) {
                val cancelledOrder = order.cancel()
                orderRepository.save(cancelledOrder)
                logger.info("Order {} cancelled and notification sent to {}", orderId, order.customerEmail)
            } else {
                logger.error("Failed to send cancellation email for order {}", null, orderId)
            }

            sent
        } catch (e: Exception) {
            logger.error("Error sending order cancellation for order {}", e, orderId)
            false
        }
    }

    fun notifyBatchOrders(orderIds: List<String>, event: NotificationEvent): BatchResult {
        logger.info("Processing batch notification for {} orders, event: {}", orderIds.size, event)

        val emails = orderIds.mapNotNull { orderId ->
            orderRepository.findById(orderId)?.let { order ->
                when (event) {
                    NotificationEvent.ORDER_CONFIRMED -> createOrderConfirmationEmail(order)
                    NotificationEvent.ORDER_SHIPPED -> createOrderShippedEmail(order)
                    NotificationEvent.ORDER_DELIVERED -> createOrderDeliveredEmail(order)
                    else -> null
                }
            }
        }

        if (emails.isEmpty()) {
            logger.warn("No valid orders found for batch notification")
            return BatchResult(successful = 0, failed = 0)
        }

        return try {
            val result = emailService.sendBatch(emails)
            logger.info("Batch notification completed: {} successful, {} failed", result.successful, result.failed)
            result
        } catch (e: Exception) {
            logger.error("Error sending batch notifications", e)
            BatchResult(successful = 0, failed = emails.size, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    private fun createOrderConfirmationEmail(order: Order): Email {
        return Email(
            recipient = order.customerEmail,
            subject = "Order Confirmation - ${order.id}",
            body = """
                Thank you for your order!
                
                Order ID: ${order.id}
                Total: ${order.total} NOK
                
                Your order has been confirmed and is being processed.
            """.trimIndent()
        )
    }

    private fun createOrderCancellationEmail(order: Order, reason: String): Email {
        return Email(
            recipient = order.customerEmail,
            subject = "Order Cancelled - ${order.id}",
            body = """
                Your order has been cancelled.
                
                Order ID: ${order.id}
                Reason: $reason
                
                If you have any questions, please contact our support.
            """.trimIndent()
        )
    }

    private fun createOrderShippedEmail(order: Order): Email {
        return Email(
            recipient = order.customerEmail,
            subject = "Order Shipped - ${order.id}",
            body = """
                Your order has been shipped!
                
                Order ID: ${order.id}
                
                You will receive it soon.
            """.trimIndent()
        )
    }

    private fun createOrderDeliveredEmail(order: Order): Email {
        return Email(
            recipient = order.customerEmail,
            subject = "Order Delivered - ${order.id}",
            body = """
                Your order has been delivered!
                
                Order ID: ${order.id}
                
                Thank you for shopping with us!
            """.trimIndent()
        )
    }
}