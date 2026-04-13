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

data class AppDependencies(
    val kundeRepository: KundeRepository,
    val lagerRepository: LagerRepository,
    val ordreValidering: OrdreValidering
)

object AppFactory {

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
