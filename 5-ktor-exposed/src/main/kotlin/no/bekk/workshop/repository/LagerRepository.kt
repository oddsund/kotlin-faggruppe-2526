package no.bekk.workshop.repository

interface LagerRepository {
    suspend fun hentBeholdning(produktId: String): Int
    suspend fun oppdaterBeholdning(produktId: String, antall: Int)
}
