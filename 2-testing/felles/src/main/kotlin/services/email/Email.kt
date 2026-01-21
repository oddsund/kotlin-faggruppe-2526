package services.email

data class Email(
    val recipient: String,
    val subject: String,
    val body: String,
    val fromAddress: String = "noreply@example.com"
)