package com.example.ordre.ws2.service.løsning

import com.example.ordre.ws2.domain.model.løsning.Kunde
import com.example.ordre.ws2.domain.model.løsning.ProduktLager
import com.example.ordre.ws2.fake.løsning.InMemoryKundePort
import com.example.ordre.ws2.fake.løsning.InMemoryProduktLagerPort
import com.example.ordre.ws2.model.ValideringsResultat
import com.example.ordre.ws2.testdata.OrdreMother
import com.example.ordre.ws2.testdata.gyldig
import com.example.ordre.ws2.testdata.inaktiv
import com.example.ordre.ws2.testdata.påLager
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// LØSNING: Refaktorert til å bruke in-memory fakes i stedet for MockK
//
// Sammenligning med original test:
// - Litt mindre setup
// - FØR: MockK stubbing med every { ... } returns ...
// - ETTER: Direct data setup med .leggTil()
// - FØR: Må importere mockk, every, verify
// - ETTER: Bare behøver in-memory fakes
//
// Fordeler:
// 1. Mer lesbar kode (ingen mocking DSL)
// 2. Enklere å forstå test setup
// 3. Kan verifisere state i stedet for interactions
// 4. Fakes fungerer som "ekte" implementasjon

class OrdreServiceTest {

    // ENDRET: Byttet fra mockk() til in-memory fakes
    // FØR: private val kundeRepository: KundeRepository = mockk()
    // ETTER: private val kundePort = InMemoryKundePort()
    private val kundePort = InMemoryKundePort()
    private val produktLagerPort = InMemoryProduktLagerPort()

    private val ordreService = OrdreService(kundePort, produktLagerPort)

    @BeforeEach
    fun setUp() {
        // Tøm fakes før hver test for å sikre isolasjon
        kundePort.tøm()
        produktLagerPort.tøm()
    }

    @Test
    fun `skal returnere TotalForLav når ordre er under minimum`() {
        val ordre = OrdreMother.ordreMedTotal(50.0)

        val resultat = ordreService.validerOrdre(ordre)

        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.TotalForLav>()
        resultat.total shouldBe 50.0
        resultat.minimum shouldBe 100.0

        // Merk: Ingen verify nødvendig - testen er fokusert på output, ikke interactions
    }

    @Test
    fun `skal returnere KundeIkkeFunnet når kunde ikke eksisterer`() {
        val ordre = OrdreMother.gyldigOrdre()

        // 2. IKKE legg til kunde i kundePort (den vil returnere null)
        // FØR: every { kundeRepository.findById(any()) } returns Optional.empty()
        // ETTER: Ingenting! Fake returnerer null by default

        val resultat = ordreService.validerOrdre(ordre)

        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeIkkeFunnet>()
        resultat.kundeId shouldBe ordre.kundeId
    }

    @Test
    fun `skal returnere KundeInaktiv når kunde ikke er aktiv`() {
        val ordre = OrdreMother.gyldigOrdre()

        // 2. Legg til inaktiv kunde
        // FØR:
        //   val inaktivKunde = KundeEntityMother.inaktivKunde()
        //   every { kundeRepository.findById(any()) } returns Optional.of(inaktivKunde)
        // ETTER:
        kundePort.leggTil(Kunde.inaktiv(id = ordre.kundeId))

        val resultat = ordreService.validerOrdre(ordre)
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeInaktiv>()
        resultat.kundeId shouldBe ordre.kundeId
    }

    @Test
    fun `skal returnere UtAvLager når produkt er utilgjengelig`() {
        val ordre = OrdreMother.ordreMedVarer("P1", "P2")

        // 2. Legg til aktiv kunde
        // FØR: 2 linjer (val aktivKunde = ..., every { ... } returns Optional.of(...))
        // ETTER: 1 linje
        kundePort.leggTil(Kunde.gyldig(id = ordre.kundeId))

        // 3. Legg til P1 på lager, men ikke P2
        // FØR:
        //   val påLagerEntity = ProduktLagerEntityMother.påLager("P1")
        //   every { produktLagerRepository.findByProduktId("P1") } returns påLagerEntity
        //   every { produktLagerRepository.findByProduktId("P2") } returns null
        // ETTER:
        produktLagerPort.leggTil(ProduktLager.påLager("P1"))
        // P2 legges ikke til, så den returnerer null

        val resultat = ordreService.validerOrdre(ordre)
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P2"
    }

    @Test
    fun `skal returnere Gyldig når alle sjekker passerer`() {
        val ordre = OrdreMother.ordreMedVarer("P1", "P2", "P3")

        // 2. Setup test data (før: 5 linjer, etter: 2 linjer!)
        // FØR:
        //   val aktivKunde = KundeEntityMother.aktivKunde()
        //   every { kundeRepository.findById(any()) } returns Optional.of(aktivKunde)
        //   every { produktLagerRepository.findByProduktId("P1") } returns ProduktLagerEntityMother.påLager("P1")
        //   every { produktLagerRepository.findByProduktId("P2") } returns ProduktLagerEntityMother.påLager("P2")
        //   every { produktLagerRepository.findByProduktId("P3") } returns ProduktLagerEntityMother.påLager("P3")
        // ETTER:
        kundePort.leggTil(Kunde.gyldig(id = ordre.kundeId))
        produktLagerPort.leggTilPåLager("P1" to 100, "P2" to 50, "P3" to 75)

        val resultat = ordreService.validerOrdre(ordre)

        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()
    }
}

/*
 * SAMMENDRAG AV FORBEDRINGER:
 *
 * 1. Mindre kode
 * 2. Enklere setup: Direct assignment vs mocking DSL
 * 3. Mer lesbar: Ingen every/returns boilerplate
 * 4. Ingen verify nødvendig: Fokuserer på output, ikke interactions
 * 5. Enklere å vedlikeholde: Færre avhengigheter (ingen MockK)
 *
 * Ports & Adapters tilfører:
 * - Domain layer (OrdreService) er uavhengig av persistence details
 * - Enkelt å bytte implementasjon (fake vs adapter vs mock)
 * - Testbar kode uten mocking frameworks
 */
