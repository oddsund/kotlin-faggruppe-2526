package services.email

data class BatchResult(
    val successful: Int,
    val failed: Int,
    val errors: List<String> = emptyList()
)