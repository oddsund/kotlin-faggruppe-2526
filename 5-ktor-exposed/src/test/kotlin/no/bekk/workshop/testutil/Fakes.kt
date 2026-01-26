package no.bekk.workshop.testutil

import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository

/**
 * In-memory fake implementasjon av KundeRepository.
 * Brukes i tester i stedet for ekte database.
 */
class FakeKundeRepository : KundeRepository {
    private val kunder = mutableMapOf<Long, Kunde>()
    private var nextId = 1L

    override suspend fun hent(id: Long): Kunde? = kunder[id]

    override suspend fun hentAlle(): List<Kunde> = kunder.values.toList()

    override suspend fun lagre(kunde: Kunde): Long {
        val id = if (kunde.id > 0) kunde.id else nextId++
        kunder[id] = kunde.copy(id = id)
        return id
    }

    override suspend fun oppdater(id: Long, erAktiv: Boolean): Boolean {
        val kunde = kunder[id] ?: return false
        kunder[id] = kunde.copy(erAktiv = erAktiv)
        return true
    }

    // Test-hjelpefunksjoner
    fun leggTil(kunde: Kunde) {
        kunder[kunde.id] = kunde
    }

    fun clear() {
        kunder.clear()
        nextId = 1L
    }
}

/**
 * In-memory fake implementasjon av LagerRepository.
 * Brukes i tester i stedet for ekte database.
 */
class FakeLagerRepository : LagerRepository {
    private val beholdning = mutableMapOf<String, Int>()

    override suspend fun hentBeholdning(produktId: String): Int =
        beholdning[produktId] ?: 0

    override suspend fun leggTil(produktId: String, antall: Int) {
        beholdning[produktId] = (beholdning[produktId] ?: 0) + antall
    }

    override suspend fun reduserBeholdning(produktId: String, antall: Int): Int {
        val current = beholdning[produktId] ?: return 0
        val newValue = maxOf(0, current - antall)
        beholdning[produktId] = newValue
        return newValue
    }

    override suspend fun slettProdukt(produktId: String): Boolean {
        return beholdning.remove(produktId) != null
    }

    // Test-hjelpefunksjoner
    fun settBeholdning(produktId: String, antall: Int) {
        beholdning[produktId] = antall
    }

    fun clear() {
        beholdning.clear()
    }
}
