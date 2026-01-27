package no.bekk.workshop.repository.løsningsforslag

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Kunder
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.repository.KundeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * LØSNINGSFORSLAG: Exposed implementasjon av KundeRepository.
 */
class KundeRepositoryExposed(private val database: Database) : KundeRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    override suspend fun hent(id: Long): Kunde? = dbQuery {
        Kunder.select(Kunder.id eq id)
            .map { it.toKunde() }
            .singleOrNull()
    }

    override suspend fun hentAlle(): List<Kunde> = dbQuery {
        Kunder.selectAll()
            .map { it.toKunde() }
    }

    override suspend fun lagre(kunde: Kunde): Long = dbQuery {
        Kunder.insert {
            it[navn] = kunde.navn
            it[erAktiv] = kunde.erAktiv
        }[Kunder.id]
    }

    override suspend fun oppdater(id: Long, erAktiv: Boolean): Boolean = dbQuery {
        Kunder.update({ Kunder.id eq id }) {
            it[Kunder.erAktiv] = erAktiv
        } > 0
    }

    private fun ResultRow.toKunde() = Kunde(
        id = this[Kunder.id],
        navn = this[Kunder.navn],
        erAktiv = this[Kunder.erAktiv]
    )
}
