package no.bekk.workshop

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
 * Composition Root - Manuell DI uten framework.
 *
 * Factory-funksjoner som oppretter hele objektgrafen.
 */
object AppFactory {

    /**
     * Oppretter en test-app med injiserte fake repositories.
     * Brukes i tester for å bytte ut ekte repos med fakes.
     *
     * TODO: Implementer denne funksjonen
     * - Ta inn kundeRepository og lagerRepository som parametere
     * - Opprett OrdreValidering med disse
     * - Returner OrdreValidering
     */
    fun createTestApp(
        kundeRepository: KundeRepository,
        lagerRepository: LagerRepository
    ): OrdreValidering {
        TODO("Implementer createTestApp - returner OrdreValidering med injiserte repositories")
    }

    /**
     * Oppretter en produksjons-app med ekte database-repositories.
     *
     * TODO: Implementer denne funksjonen
     * - Opprett H2 in-memory database
     * - Opprett tabeller med SchemaUtils.create
     * - Opprett KundeRepositoryExposed og LagerRepositoryExposed
     * - Opprett og returner OrdreValidering
     */
    fun createProductionApp(): OrdreValidering {
        TODO("Implementer createProductionApp - opprett database, repos og OrdreValidering")
    }

    /**
     * Hjelpefunksjon for å opprette og initialisere database.
     */
    fun createDatabase(): Database {
        val database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction(database) {
            SchemaUtils.create(Kunder, Lager)
        }
        return database
    }
}
