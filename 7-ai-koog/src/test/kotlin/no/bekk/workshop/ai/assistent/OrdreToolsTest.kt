package no.bekk.workshop.ai.assistent

import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.ai.assistent.løsningsforslag.OrdreToolsLøsning
import no.bekk.workshop.ai.produkt.ProduktKatalog
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.testutil.FakeKundeRepository
import no.bekk.workshop.testutil.FakeLagerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrdreToolsTest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository
    private lateinit var tools: OrdreToolsLøsning

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
        tools = OrdreToolsLøsning(kundeRepository, lagerRepository, ProduktKatalog())
    }

    @Test
    fun `slåOppKunde returnerer kundens navn og status`() = runTest {
        kundeRepository.leggTil(Kunde(id = 42, navn = "Ola Nordmann", erAktiv = true))

        val resultat = tools.slåOppKunde(42)

        resultat shouldContain "Ola Nordmann"
        resultat shouldContain "aktiv"
    }

    @Test
    fun `sjekkLager returnerer beholdning for eksisterende produkt`() = runTest {
        lagerRepository.settBeholdning("P001", 7)

        val resultat = tools.sjekkLager("P001")

        resultat shouldContain "P001"
        resultat shouldContain "7"
    }

    @Test
    fun `sjekkLager returnerer 0 for ukjent produkt`() = runTest {
        val resultat = tools.sjekkLager("UKJENT")

        resultat shouldContain "0"
    }
}
