package no.bekk.workshop.repository

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Kunder
import no.bekk.workshop.domain.Kunde
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class KundeRepositoryExposed(private val database: Database) : KundeRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    override suspend fun hent(id: Long): Kunde? = dbQuery {
        // TODO: Hent kunde fra Kunder-tabellen basert på id
        // Tips: Se på Exposed DSL for select og where
        TODO("Implementer hent")
    }

    override suspend fun hentAlle(): List<Kunde> = dbQuery {
        // TODO (Ekstra): Hent alle kunder fra tabellen
        // Tips: selectAll() returnerer alle rader
        TODO("Implementer hentAlle")
    }

    override suspend fun lagre(kunde: Kunde): Long = dbQuery {
        // TODO: Sett inn ny kunde og returner generert id
        // Tips: Se på insert-funksjonen i Exposed
        TODO("Implementer lagre")
    }

    override suspend fun oppdater(id: Long, erAktiv: Boolean): Boolean = dbQuery {
        // TODO: Oppdater erAktiv-feltet for kunde med gitt id
        // Tips: update returnerer antall rader som ble endret
        TODO("Implementer oppdater")
    }

    private fun ResultRow.toKunde() = Kunde(
        id = this[Kunder.id],
        navn = this[Kunder.navn],
        erAktiv = this[Kunder.erAktiv]
    )
}
