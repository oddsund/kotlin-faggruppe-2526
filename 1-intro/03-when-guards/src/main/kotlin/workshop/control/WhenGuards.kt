package workshop.control

// Delt opp `is ApiResponse.Error` i to, med guard for 401 statusen
fun handleResponse(resp: ApiResponse): String =
    when (resp) {
        is ApiResponse.Success -> resp.handleSuccess()
        is ApiResponse.Error if resp.code == 401 -> "auth-error"
        is ApiResponse.Error -> resp.handleError()
    }
