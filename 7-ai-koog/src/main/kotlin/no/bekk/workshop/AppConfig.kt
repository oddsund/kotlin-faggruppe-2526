package no.bekk.workshop

data class AppConfig(
    val databaseUrl: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    val databaseDriver: String = "org.h2.Driver"
) {
    companion object {
        fun fromEnvironment(): AppConfig {
            return AppConfig(
                databaseUrl = System.getenv("DATABASE_URL") ?: "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                databaseDriver = System.getenv("DATABASE_DRIVER") ?: "org.h2.Driver"
            )
        }
    }
}
