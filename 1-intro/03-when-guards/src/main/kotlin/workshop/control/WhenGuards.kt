package workshop.control

fun handleResponse(resp: ApiResponse): String {
    return when (resp) {
        is ApiResponse.Success -> resp.handleSuccess()
        is ApiResponse.Error -> {
            if (resp.code == 401) {
                "auth-error"
            } else {
                resp.handleError()
            }
        }
    }
}
