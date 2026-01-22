package no.bekk.workshop.testutil

import no.bekk.workshop.repository.LagerRepository

/**
 * In-memory fake implementasjon av LagerRepository.
 * Brukes i tester i stedet for ekte database.
 */
class FakeLagerRepository : LagerRepository {
    private val beholdning = mutableMapOf<String, Int>()

    override suspend fun hentBeholdning(produktId: String): Int = beholdning[produktId] ?: 0

    override suspend fun oppdaterBeholdning(produktId: String, antall: Int) {
        beholdning[produktId] = antall
    }

    // Test-hjelpefunksjoner
    fun settBeholdning(produktId: String, antall: Int) {
        beholdning[produktId] = antall
    }

    fun settBeholdningForAlle(vararg produkter: Pair<String, Int>) {
        produkter.forEach { (produktId, antall) -> beholdning[produktId] = antall }
    }

    fun clear() {
        beholdning.clear()
    }
}
