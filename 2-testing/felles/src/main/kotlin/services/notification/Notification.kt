package services.notification

data class Notification(
    val event: NotificationEvent,
    val orderId: String,
    val customerId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)