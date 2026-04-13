package no.bekk.workshop.domain

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrdreValideringTest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository
    private lateinit var ordreValidering: OrdreValidering

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
        ordreValidering = OrdreValidering(kundeRepository, lagerRepository)
    }

    @Test
    fun `gyldig ordre returnerer Gyldig`() = runTest {
        // Arrange
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        lagerRepository.settBeholdning("P1", 10)

        val ordre = Ordre.gyldig(kundeId = 1)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()
    }

    @Test
    fun `ordre under minimum total returnerer TotalForLav`() = runTest {
        // Arrange
        val ordre = Ordre.underMinimum()

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.TotalForLav>()
        resultat.total shouldBe 50.0
        resultat.minimum shouldBe 100.0
    }

    @Test
    fun `ordre med ukjent kunde returnerer KundeIkkeFunnet`() = runTest {
        // Arrange - ingen kunde lagt til
        val ordre = Ordre.gyldig(kundeId = 999)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeIkkeFunnet>()
        resultat.kundeId shouldBe 999
    }

    @Test
    fun `ordre med inaktiv kunde returnerer KundeInaktiv`() = runTest {
        // Arrange
        kundeRepository.leggTil(Kunde.inaktiv(id = 1))

        val ordre = Ordre.gyldig(kundeId = 1)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeInaktiv>()
        resultat.kundeId shouldBe 1
    }

    @Test
    fun `ordre med produkt uten lager returnerer UtAvLager`() = runTest {
        // Arrange
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        // Ingen lagerbeholdning satt

        val ordre = Ordre.medProdukter("P1", "P2", kundeId = 1)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P1" // Første vare som mangler
    }

    @Test
    fun `ordre med noen produkter på lager og noen tom returnerer UtAvLager for det tomme`() = runTest {
        // Arrange
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        lagerRepository.settBeholdning("P1", 10)
        // P2 har ingen beholdning

        val ordre = Ordre.medProdukter("P1", "P2", kundeId = 1)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P2"
    }
}
