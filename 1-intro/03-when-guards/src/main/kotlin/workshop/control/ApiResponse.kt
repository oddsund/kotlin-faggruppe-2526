package workshop.control

sealed interface ApiResponse {
    data class Success(val data: String) : ApiResponse {
        fun handleSuccess() = "processed:$data"
    }

    data class Error(val code: Int, val message: String) : ApiResponse {
        fun handleError() = "error:$code:$message"
    }
}