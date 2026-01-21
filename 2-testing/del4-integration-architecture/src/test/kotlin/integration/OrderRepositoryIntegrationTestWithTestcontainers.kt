package integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import services.order.OrderRepository

// TODO: Fjern @Disabled når du er klar til å kjøre med Testcontainers
@Disabled("Requires Docker - enable when ready")
@Testcontainers
class OrderRepositoryIntegrationTestWithTestcontainers {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }
    }

    private lateinit var repository: OrderRepository

    @BeforeEach
    fun setup() {
        // TODO: Initialiser database med Testcontainers URL
        // Hint: DatabaseFactory.init(postgres.jdbcUrl, "org.postgresql.Driver", postgres.username, postgres.password)

        // TODO: Opprett repository

        // TODO: Reset database state
    }

    @Test
    fun `skal lagre og hente ordre fra database`() {
        // TODO: Opprett en ordre med integration.anOrder()

        // TODO: Lagre ordre

        // TODO: Hent ordre fra database

        // TODO: Verifiser at alle felter er korrekte
        // TODO: Verifiser at items er korrekt lagret
    }

    @Test
    fun `skal oppdatere eksisterende ordre`() {
        // TODO: Lagre en ordre

        // TODO: Oppdater ordre (endre status til CONFIRMED)

        // TODO: Hent ordre igjen

        // TODO: Verifiser at status er oppdatert
    }

    @Test
    fun `skal hente alle ordrer for en kunde`() {
        // TODO: Opprett og lagre 3 ordrer for kunde1

        // TODO: Opprett og lagre 2 ordrer for kunde2

        // TODO: Hent ordrer for kunde1

        // TODO: Verifiser at kun kunde1 sine ordrer returneres
    }

    @Test
    fun `skal returnere null for ordre som ikke finnes`() {
        // TODO: Prøv å hente ordre med ID som ikke finnes

        // TODO: Verifiser at resultatet er null
    }

    @Test
    fun `skal håndtere ordre med mange items`() {
        // TODO: Opprett ordre med 10 items

        // TODO: Lagre og hent ordre

        // TODO: Verifiser at alle 10 items er lagret korrekt
    }
}

