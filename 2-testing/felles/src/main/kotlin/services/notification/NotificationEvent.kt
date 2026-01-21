package services.notification

enum class NotificationEvent {
    ORDER_CONFIRMED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED,
    PAYMENT_RECEIVED,
    PAYMENT_FAILED
}