package workshop.control

import kotlin.test.Test
import kotlin.test.assertEquals

class HandleResponseTest {
    @Test
    fun `handles success`() {
        val result = handleResponse(ApiResponse.Success("data"))
        assertEquals("processed:data", result)
    }

    @Test
    fun `handles auth error`() {
        val result = handleResponse(ApiResponse.Error(401, "Unauthorized"))
        assertEquals("auth-error", result)
    }

    @Test
    fun `handles other error`() {
        val result = handleResponse(ApiResponse.Error(500, "Server down"))
        assertEquals("error:500:Server down", result)
    }
}
