package no.bekk.workshop.di

import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository
import org.koin.dsl.module

// === Oppgave 3: testModule  ===
/**
 * Koin modul for testing med fake repositories.
 * Brukes sammen med testApplication { }.
 */
fun testModule(
    kundeRepository: KundeRepository,
    lagerRepository: LagerRepository
) = module {
    // TODO: Registrer fake repositories og OrdreValidering
    // Tips: Bruk single { } for å registrere dependencies
}

// === Oppgave 4: appModule  ===
/**
 * Koin modul for produksjon med ekte database.
 */
val appModule = module {
    // TODO: Registrer Database, KundeRepositoryExposed, LagerRepositoryExposed og OrdreValidering
    // Tips: Bruk single<Interface> { Implementation(get()) } for repositories
}
