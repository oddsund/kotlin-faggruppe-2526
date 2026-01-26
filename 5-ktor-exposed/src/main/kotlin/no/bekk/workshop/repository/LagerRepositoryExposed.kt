package no.bekk.workshop.repository

import kotlinx.coroutines.Dispatchers
import no.bekk.workshop.db.Lager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LagerRepositoryExposed(private val database: Database) : LagerRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    override suspend fun hentBeholdning(produktId: String): Int = dbQuery {
        Lager.select(Lager.produktId eq produktId)
            .map { it[Lager.antall] }
            .singleOrNull() ?: 0
    }

    override suspend fun leggTil(produktId: String, antall: Int): Unit = dbQuery {
        val eksisterer = Lager.select(Lager.produktId eq produktId).count() > 0

        if (eksisterer) {
            Lager.update({ Lager.produktId eq produktId }) {
                it[Lager.antall] = antall
            }
        } else {
            Lager.insert {
                it[Lager.produktId] = produktId
                it[Lager.antall] = antall
            }
        }
    }

    override suspend fun reduserBeholdning(produktId: String, antall: Int): Int = dbQuery {
        val nåværende = Lager.select(Lager.produktId eq produktId)
            .map { it[Lager.antall] }
            .singleOrNull() ?: return@dbQuery 0

        val nyBeholdning = maxOf(0, nåværende - antall)

        Lager.update({ Lager.produktId eq produktId }) {
            it[Lager.antall] = nyBeholdning
        }

        nyBeholdning
    }

    override suspend fun slettProdukt(produktId: String): Boolean = dbQuery {
        val slettet = Lager.deleteWhere { Lager.produktId eq produktId }
        slettet > 0
    }
}
