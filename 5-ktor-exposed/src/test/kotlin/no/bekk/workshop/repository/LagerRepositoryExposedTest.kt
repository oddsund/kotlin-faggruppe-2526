package no.bekk.workshop.repository

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.db.Lager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LagerRepositoryExposedTest {

    private lateinit var database: Database
    private lateinit var repository: LagerRepositoryExposed

    @BeforeEach
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test_lager_${System.nanoTime()};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction(database) {
            SchemaUtils.create(Lager)
        }
        repository = LagerRepositoryExposed(database)
    }

    @Test
    fun `hentBeholdning returnerer antall når produkt finnes`() = runTest {
        // Arrange
        repository.oppdaterBeholdning("P1", 50)

        // Act
        val beholdning = repository.hentBeholdning("P1")

        // Assert
        beholdning shouldBe 50
    }

    @Test
    fun `hentBeholdning returnerer 0 når produkt ikke finnes`() = runTest {
        // Act
        val beholdning = repository.hentBeholdning("UKJENT")

        // Assert
        beholdning shouldBe 0
    }

    @Test
    fun `oppdaterBeholdning setter ny beholdning`() = runTest {
        // Act
        repository.oppdaterBeholdning("P2", 100)

        // Assert
        repository.hentBeholdning("P2") shouldBe 100
    }

    @Test
    fun `oppdaterBeholdning kan oppdatere eksisterende beholdning`() = runTest {
        // Arrange
        repository.oppdaterBeholdning("P3", 10)

        // Act
        repository.oppdaterBeholdning("P3", 25)

        // Assert
        repository.hentBeholdning("P3") shouldBe 25
    }
}
