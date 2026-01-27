package no.bekk.workshop.løsningsforslag

import no.bekk.workshop.AppConfig
import no.bekk.workshop.db.Kunder
import no.bekk.workshop.db.Lager
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.KundeRepositoryExposed
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.repository.LagerRepositoryExposed
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * LØSNINGSFORSLAG: Holder alle dependencies som trengs for appen.
 */
data class AppDependencies(
    val kundeRepository: KundeRepository,
    val lagerRepository: LagerRepository,
    val ordreValidering: OrdreValidering
)

/**
 * LØSNINGSFORSLAG: Composition Root - Manuell DI uten framework.
 * Factory-funksjoner som oppretter hele objektgrafen.
 */
object AppFactory {

    // Oppgave 1: createTestApp
    /**
     * Oppretter dependencies med fake repositories for testing.
     */
    fun createTestApp(
        kundeRepository: KundeRepository,
        lagerRepository: LagerRepository
    ): AppDependencies {
        val ordreValidering = OrdreValidering(kundeRepository, lagerRepository)
        return AppDependencies(
            kundeRepository = kundeRepository,
            lagerRepository = lagerRepository,
            ordreValidering = ordreValidering
        )
    }

    // Oppgave 2: createProductionApp
    /**
     * Oppretter dependencies med ekte database-repositories.
     */
    fun createProductionApp(config: AppConfig = AppConfig()): AppDependencies {
        val database = createDatabase(config)
        val kundeRepository = KundeRepositoryExposed(database)
        val lagerRepository = LagerRepositoryExposed(database)
        val ordreValidering = OrdreValidering(kundeRepository, lagerRepository)

        return AppDependencies(
            kundeRepository = kundeRepository,
            lagerRepository = lagerRepository,
            ordreValidering = ordreValidering
        )
    }

    /**
     * Hjelpefunksjon for å opprette og initialisere database.
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
