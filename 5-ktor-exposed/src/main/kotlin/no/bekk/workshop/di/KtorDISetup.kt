package no.bekk.workshop.di

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

// === Oppgave: Konfigurer Ktor DI  ===
/**
 * Konfigurerer Ktor's innebygde DI med produksjons-dependencies.
 * Bruker provide { } for å registrere dependencies.
 */
fun Application.configureDependencies(config: AppConfig = AppConfig()) {
    dependencies {
        // TODO: Registrer Database med provide { }
        // Tips: Bruk AppFactory.createDatabase(config)

        // TODO: Registrer KundeRepository
        // Tips: provide<KundeRepository> { KundeRepositoryExposed(resolve()) }

        // TODO: Registrer LagerRepository

        // TODO: Registrer OrdreValidering
        // Tips: OrdreValidering tar to repositories som parametere
    }
}

/**
 * Konfigurerer Ktor DI for testing med fake repositories.
 * Tar inn ferdigopprettede fakes som parametere.
 */
fun Application.configureTestDependencies(
    kundeRepository: KundeRepository,
    lagerRepository: LagerRepository
) {
    dependencies {
        // TODO: Registrer fake repositories og OrdreValidering
        // Tips: provide<Interface> { instans }
    }
}
