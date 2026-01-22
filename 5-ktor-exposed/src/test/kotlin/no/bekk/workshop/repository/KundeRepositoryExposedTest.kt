package no.bekk.workshop.repository

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.db.Kunder
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.testutil.KundeMother
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KundeRepositoryExposedTest {

    private lateinit var database: Database
    private lateinit var repository: KundeRepositoryExposed

    @BeforeEach
    fun setup() {
        database = Database.connect(
            url = "jdbc:h2:mem:test_kunde_${System.nanoTime()};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction(database) {
            SchemaUtils.create(Kunder)
        }
        repository = KundeRepositoryExposed(database)
    }

    @Test
    fun `hent returnerer kunde når den finnes`() = runTest {
        // Arrange - lagre en kunde først
        val kunde = KundeMother.aktivKunde(navn = "Test Testesen")
        val id = repository.lagre(kunde)

        // Act
        val hentet = repository.hent(id)

        // Assert
        hentet.shouldNotBeNull()
        hentet.id shouldBe id
        hentet.navn shouldBe "Test Testesen"
        hentet.erAktiv shouldBe true
    }

    @Test
    fun `hent returnerer null når kunde ikke finnes`() = runTest {
        // Act
        val hentet = repository.hent(999)

        // Assert
        hentet.shouldBeNull()
    }

    @Test
    fun `lagre returnerer generert id`() = runTest {
        // Arrange
        val kunde = Kunde(id = 0, navn = "Ny Kunde", erAktiv = true)

        // Act
        val id = repository.lagre(kunde)

        // Assert
        id shouldBe 1L
    }

    @Test
    fun `lagre kan lagre flere kunder med ulike ider`() = runTest {
        // Arrange & Act
        val id1 = repository.lagre(Kunde(0, "Kunde 1", true))
        val id2 = repository.lagre(Kunde(0, "Kunde 2", false))

        // Assert
        id1 shouldBe 1L
        id2 shouldBe 2L

        val kunde1 = repository.hent(id1)
        val kunde2 = repository.hent(id2)

        kunde1?.navn shouldBe "Kunde 1"
        kunde1?.erAktiv shouldBe true
        kunde2?.navn shouldBe "Kunde 2"
        kunde2?.erAktiv shouldBe false
    }
}
