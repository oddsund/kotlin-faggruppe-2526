package no.bekk.workshop.repository

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Lager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LagerRepositoryExposed(private val database: Database) : LagerRepository {

    override suspend fun hentBeholdning(produktId: String): Int = newSuspendedTransaction(Dispatchers.IO, database) {
        // TODO: Implementer henting av beholdning fra databasen
        // Bruk: Lager.selectAll().where { Lager.produktId eq produktId }.singleOrNull()
        // Returner antall eller 0 hvis ikke funnet
        TODO("Implementer hentBeholdning(produktId) - returner antall eller 0")
    }

    override suspend fun oppdaterBeholdning(produktId: String, antall: Int): Unit = newSuspendedTransaction(Dispatchers.IO, database) {
        // TODO: Implementer oppdatering/innsetting av beholdning
        // Sjekk om produktet finnes, hvis ja: update, hvis nei: insert
        TODO("Implementer oppdaterBeholdning(produktId, antall)")
    }
}
