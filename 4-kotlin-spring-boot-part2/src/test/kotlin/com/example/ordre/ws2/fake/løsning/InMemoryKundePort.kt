package com.example.ordre.ws2.fake.løsning

import com.example.ordre.ws2.domain.model.løsning.Kunde
import com.example.ordre.ws2.domain.port.løsning.KundePort

/**
 * In-memory fake implementasjon av KundePort for testing
 *
 * Hvorfor fake i stedet for mock?
 * - Mer lesbar testkode: `kundePort.leggTil(Kunde.gyldig())` vs `every { ... } returns ...`
 * - Ingen stubbing-boilerplate - bare legg til data direkte
 * - Enklere å gjenbruke i flere tester
 * - Kan verifisere state i stedet for interactions
 * - Fungerer som "ekte" implementasjon - god for å oppdage bugs i logikken
 *
 * Sammenligning:
 *   Mock:  every { kundePort.hentKunde(123) } returns Optional.of(testKunde)
 *   Fake:  kundePort.leggTil(Kunde(id = 123, navn = "Test", erAktiv = true))
 */
class InMemoryKundePort : KundePort {

    private val kunder = mutableMapOf<Long, Kunde>()

    override fun hentKunde(kundeId: Long): Kunde? {
        return kunder[kundeId]
    }

    fun leggTil(kunde: Kunde) {
        kunder[kunde.id] = kunde
    }

    /**
     * Tømmer all data - nyttig i @BeforeEach for å starte med "tom database"
     * Alternativt kan det opprettes en ny InMemoryKundePort i hver test
     */
    fun tøm() {
        kunder.clear()
    }
}
