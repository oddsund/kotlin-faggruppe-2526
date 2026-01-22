package no.bekk.workshop.di

import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.KundeRepositoryExposed
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.repository.LagerRepositoryExposed
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

/**
 * Koin modul for produksjonsavhengigheter.
 *
 * TODO: Definer bindings for:
 * - Database (single)
 * - KundeRepository -> KundeRepositoryExposed (single)
 * - LagerRepository -> LagerRepositoryExposed (single)
 * - OrdreValidering (single)
 */
val appModule = module {
    // TODO: Implementer Koin module
    // Eksempel:
    // single<Database> { Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver") }
    // single<KundeRepository> { KundeRepositoryExposed(get()) }
    // single<LagerRepository> { LagerRepositoryExposed(get()) }
    // single { OrdreValidering(get(), get()) }
}
