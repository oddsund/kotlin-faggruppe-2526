package no.bekk.workshop.repository

interface LagerRepository {
    suspend fun hentBeholdning(produktId: String): Int
    suspend fun leggTil(produktId: String, antall: Int)
    suspend fun reduserBeholdning(produktId: String, antall: Int): Int
    suspend fun slettProdukt(produktId: String): Boolean
}
