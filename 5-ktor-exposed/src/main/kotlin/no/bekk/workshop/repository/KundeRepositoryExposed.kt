package no.bekk.workshop.repository

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Kunder
import no.bekk.workshop.domain.Kunde
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class KundeRepositoryExposed(private val database: Database) : KundeRepository {

    override suspend fun hent(id: Long): Kunde? = newSuspendedTransaction(Dispatchers.IO, database) {
        // TODO: Implementer henting av kunde fra databasen
        // Bruk: Kunder.selectAll().where { Kunder.id eq id }.singleOrNull()
        // Map ResultRow til Kunde med: row[Kunder.id], row[Kunder.navn], row[Kunder.erAktiv]
        TODO("Implementer hent(id) - bruk selectAll().where { } og map til Kunde")
    }

    override suspend fun lagre(kunde: Kunde): Long = newSuspendedTransaction(Dispatchers.IO, database) {
        // TODO: Implementer lagring av kunde til databasen
        // Bruk: Kunder.insert { it[navn] = kunde.navn; it[erAktiv] = kunde.erAktiv }
        // Returner generert ID med: [Kunder.id]
        TODO("Implementer lagre(kunde) - bruk insert { } og returner generert id")
    }
}
