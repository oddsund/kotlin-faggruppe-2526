package no.bekk.workshop

import no.bekk.workshop.db.Kunder
import no.bekk.workshop.db.Lager
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Holder alle dependencies som trengs for appen.
 */
data class AppDependencies(
    val kundeRepository: KundeRepository,
    val lagerRepository: LagerRepository,
    val ordreValidering: OrdreValidering
)

/**
 * Composition Root - Manuell DI uten framework.
 * Factory-funksjoner som oppretter hele objektgrafen.
 */
object AppFactory {

    // === Oppgave 1: createTestApp  ===
    /**
     * Oppretter dependencies med fake repositories for testing.
     */
    fun createTestApp(
        kundeRepository: KundeRepository,
        lagerRepository: LagerRepository
    ): AppDependencies {
        // TODO: Opprett OrdreValidering og returner AppDependencies
        // Tips: OrdreValidering tar kundeRepository og lagerRepository som parametere
        TODO("Implementer createTestApp")
    }

    // === Oppgave 2: createProductionApp  ===
    /**
     * Oppretter dependencies med ekte database-repositories.
     */
    fun createProductionApp(config: AppConfig = AppConfig()): AppDependencies {
        // TODO: Opprett database, repositories og OrdreValidering
        // Tips: Bruk createDatabase() og Exposed repository-implementasjonene
        TODO("Implementer createProductionApp")
    }

    /**
     * Hjelpefunksjon for å opprette og initialisere database.
     * Denne er ferdig implementert.
     */
    fun createDatabase(config: AppConfig = AppConfig()): Database {
        val database = Database.connect(
            url = config.databaseUrl,
            driver = config.databaseDriver
        )
        transaction(database) {
            SchemaUtils.create(Kunder, Lager)
        }
        return database
    }
}
