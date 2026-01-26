package no.bekk.workshop.repository

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Lager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LagerRepositoryExposed(private val database: Database) : LagerRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    override suspend fun hentBeholdning(produktId: String): Int = dbQuery {
        // TODO: Hent beholdning for produktet, returner 0 hvis ikke funnet
        // Tips: Bruk select med where-clause
        TODO("Implementer hentBeholdning")
    }

    override suspend fun leggTil(produktId: String, antall: Int): Unit = dbQuery {
        // TODO: Legg til eller oppdater beholdning (upsert)
        // Tips: Sjekk om produktet finnes først, deretter insert eller update
        TODO("Implementer leggTil")
    }

    override suspend fun reduserBeholdning(produktId: String, antall: Int): Int = dbQuery {
        // TODO: Reduser beholdning og returner ny beholdning
        // Tips: Hent nåværende beholdning, trekk fra, oppdater, returner
        TODO("Implementer reduserBeholdning")
    }

    override suspend fun slettProdukt(produktId: String): Boolean = dbQuery {
        // TODO (Ekstra): Slett produkt fra lager, returner true hvis slettet
        // Tips: deleteWhere returnerer antall slettede rader
        TODO("Implementer slettProdukt")
    }
}
