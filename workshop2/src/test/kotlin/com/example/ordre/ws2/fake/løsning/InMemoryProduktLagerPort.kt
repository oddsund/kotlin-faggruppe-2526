package com.example.ordre.ws2.fake.løsning

import com.example.ordre.ws2.domain.model.løsning.ProduktLager
import com.example.ordre.ws2.domain.port.løsning.ProduktLagerPort

/**
 * In-memory fake implementasjon av ProduktLagerPort for testing
 *
 * Samme fordeler som InMemoryKundePort - enklere å sette opp testdata
 * uten MockK's every { ... } returns ... boilerplate
 */
class InMemoryProduktLagerPort : ProduktLagerPort {

    private val produkter = mutableMapOf<String, ProduktLager>()

    override fun hentLagerStatus(produktId: String): ProduktLager? {
        return produkter[produktId]
    }

    fun leggTil(produktLager: ProduktLager) {
        produkter[produktLager.produktId] = produktLager
    }

    /**
     * Convenience metode for å legge til flere produkter samtidig
     *
     * Eksempel:
     *   produktLagerPort.leggTilPåLager(
     *       "P1" to 100,
     *       "P2" to 50,
     *       "P3" to 0
     *   )
     */
    fun leggTilPåLager(vararg produkter: Pair<String, Int>) {
        produkter.forEach { (produktId, antall) ->
            leggTil(ProduktLager(produktId, antall))
        }
    }

    /**
     * Tømmer all data - nyttig i @BeforeEach
     * Alternativt kan det opprettes en ny InMemoryProduktLagerPort i hver test
     */
    fun tøm() {
        produkter.clear()
    }
}
