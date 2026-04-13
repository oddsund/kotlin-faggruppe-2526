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
    fun `hentBeholdning returnerer antall på lager`() = runTest {
        // Arrange
        repository.leggTil("P1", 50)

        // Act
        val beholdning = repository.hentBeholdning("P1")

        // Assert
        beholdning shouldBe 50
    }

    @Test
    fun `hentBeholdning returnerer 0 for ukjent produkt`() = runTest {
        // Act
        val beholdning = repository.hentBeholdning("UKJENT")

        // Assert
        beholdning shouldBe 0
    }

    @Test
    fun `leggTil oppretter nytt produkt`() = runTest {
        // Act
        repository.leggTil("P2", 100)

        // Assert
        repository.hentBeholdning("P2") shouldBe 100
    }

    @Test
    fun `leggTil oppdaterer eksisterende produkt`() = runTest {
        // Arrange
        repository.leggTil("P3", 10)

        // Act
        repository.leggTil("P3", 25)

        // Assert
        repository.hentBeholdning("P3") shouldBe 25
    }

    @Test
    fun `reduserBeholdning trekker fra og returnerer ny beholdning`() = runTest {
        // Arrange
        repository.leggTil("P4", 100)

        // Act
        val nyBeholdning = repository.reduserBeholdning("P4", 30)

        // Assert
        nyBeholdning shouldBe 70
        repository.hentBeholdning("P4") shouldBe 70
    }

    @Test
    fun `reduserBeholdning returnerer 0 for ukjent produkt`() = runTest {
        // Act
        val nyBeholdning = repository.reduserBeholdning("UKJENT", 10)

        // Assert
        nyBeholdning shouldBe 0
    }

    @Test
    fun `slettProdukt fjerner produkt fra lager`() = runTest {
        // Arrange
        repository.leggTil("P5", 50)

        // Act
        val slettet = repository.slettProdukt("P5")

        // Assert
        slettet shouldBe true
        repository.hentBeholdning("P5") shouldBe 0
    }

    @Test
    fun `slettProdukt returnerer false for ukjent produkt`() = runTest {
        // Act
        val slettet = repository.slettProdukt("UKJENT")

        // Assert
        slettet shouldBe false
    }
}
