package workshop.legacy

// Refaktorert: idiomatisk Kotlin, data class + null-safety
data class User(val id: String, val name: String)

class ExampleService {
    fun findUserById(id: String?): User? = id?.let { User(it, "TestUser") }
}
