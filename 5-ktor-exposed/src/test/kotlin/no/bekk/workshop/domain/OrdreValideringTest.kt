package no.bekk.workshop.domain

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.testutil.FakeKundeRepository
import no.bekk.workshop.testutil.FakeLagerRepository
import no.bekk.workshop.testutil.KundeMother
import no.bekk.workshop.testutil.OrdreMother
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
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        val ordre = OrdreMother.gyldigOrdre()

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()
    }

    @Test
    fun `ordre under minimum total returnerer TotalForLav`() = runTest {
        // Arrange
        val ordre = OrdreMother.ordreMedTotal(50.0)

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
        val ordre = OrdreMother.ordreMedKunde(999)

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeIkkeFunnet>()
        resultat.kundeId shouldBe 999
    }

    @Test
    fun `ordre med inaktiv kunde returnerer KundeInaktiv`() = runTest {
        // Arrange
        kundeRepository.leggTil(KundeMother.inaktivKunde(id = 123))

        val ordre = OrdreMother.gyldigOrdre()

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeInaktiv>()
        resultat.kundeId shouldBe 123
    }

    @Test
    fun `ordre med produkt uten lager returnerer UtAvLager`() = runTest {
        // Arrange
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        // Ingen lagerbeholdning satt

        val ordre = OrdreMother.ordreMedVarer("P1", "P2")

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P1" // Første vare som mangler
    }

    @Test
    fun `ordre med noen produkter på lager og noen tom returnerer UtAvLager for det tomme`() = runTest {
        // Arrange
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)
        // P2 har ingen beholdning

        val ordre = OrdreMother.ordreMedVarer("P1", "P2")

        // Act
        val resultat = ordreValidering.valider(ordre)

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P2"
    }
}
