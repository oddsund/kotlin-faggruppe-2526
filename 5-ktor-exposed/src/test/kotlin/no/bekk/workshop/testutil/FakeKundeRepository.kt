package no.bekk.workshop.testutil

import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.repository.KundeRepository

/**
 * In-memory fake implementasjon av KundeRepository.
 * Brukes i tester i stedet for ekte database.
 */
class FakeKundeRepository : KundeRepository {
    private val kunder = mutableMapOf<Long, Kunde>()
    private var nextId = 1L

    override suspend fun hent(id: Long): Kunde? = kunder[id]

    override suspend fun lagre(kunde: Kunde): Long {
        val id = if (kunde.id > 0) kunde.id else nextId++
        kunder[id] = kunde.copy(id = id)
        return id
    }

    // Test-hjelpefunksjoner
    fun leggTil(kunde: Kunde) {
        kunder[kunde.id] = kunde
    }

    fun leggTilAlle(vararg kundeliste: Kunde) {
        kundeliste.forEach { leggTil(it) }
    }

    fun clear() {
        kunder.clear()
        nextId = 1L
    }
}
