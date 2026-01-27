package no.bekk.workshop.di.løsningsforslag

import no.bekk.workshop.AppConfig
import no.bekk.workshop.AppFactory
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.KundeRepositoryExposed
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.repository.LagerRepositoryExposed
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

/**
 * LØSNINGSFORSLAG: Koin modul for testing med fake repositories.
 * Brukes sammen med testApplication { }.
 */
fun testModule(
    kundeRepository: KundeRepository,
    lagerRepository: LagerRepository
) = module {
    single<KundeRepository> { kundeRepository }
    single<LagerRepository> { lagerRepository }
    single { OrdreValidering(get(), get()) }
}

/**
 * LØSNINGSFORSLAG: Koin modul for produksjon med ekte database.
 */
val appModule = module {
    single<Database> { AppFactory.createDatabase(AppConfig()) }
    single<KundeRepository> { KundeRepositoryExposed(get()) }
    single<LagerRepository> { LagerRepositoryExposed(get()) }
    single { OrdreValidering(get(), get()) }
}
