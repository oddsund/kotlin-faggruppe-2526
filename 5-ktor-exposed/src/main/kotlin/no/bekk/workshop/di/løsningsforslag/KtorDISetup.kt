package no.bekk.workshop.di.løsningsforslag

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.bekk.workshop.AppConfig
import no.bekk.workshop.AppFactory
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.KundeRepositoryExposed
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.repository.LagerRepositoryExposed
import org.jetbrains.exposed.sql.Database

/**
 * LØSNINGSFORSLAG: Konfigurerer Ktor's innebygde DI med produksjons-dependencies.
 * Bruker provide { } for å registrere dependencies.
 */
fun Application.configureDependencies(config: AppConfig = AppConfig()) {
    dependencies {
        provide<Database> { AppFactory.createDatabase(config) }
        provide<KundeRepository> { KundeRepositoryExposed(resolve()) }
        provide<LagerRepository> { LagerRepositoryExposed(resolve()) }
        provide<OrdreValidering> { OrdreValidering(resolve(), resolve()) }
    }
}

/**
 * LØSNINGSFORSLAG: Konfigurerer Ktor DI for testing med fake repositories.
 * Tar inn ferdigopprettede fakes som parametere.
 */
fun Application.configureTestDependencies(
    kundeRepository: KundeRepository,
    lagerRepository: LagerRepository
) {
    dependencies {
        provide<KundeRepository> { kundeRepository }
        provide<LagerRepository> { lagerRepository }
        provide<OrdreValidering> { OrdreValidering(resolve(), resolve()) }
    }
}
