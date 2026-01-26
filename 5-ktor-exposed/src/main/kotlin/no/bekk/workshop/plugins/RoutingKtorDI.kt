package no.bekk.workshop.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository

/**
 * Konfigurerer Ktor routing med Ktor's innebygde DI.
 * Dependencies hentes med `by dependencies`.
 */
fun Application.configureRoutingKtorDI() {
    // === Oppgave: Inject dependencies fra Ktor DI ===
    // TODO: Hent OrdreValidering med `by dependencies`
    // Tips: val ordreValidering: OrdreValidering by dependencies

    // TODO: Hent KundeRepository med `by dependencies`

    routing {
        // TODO: Implementer samme routes som i Routing.kt
        // Tips: Bruk de injiserte dependencies
        // - GET /health -> returnerer "OK"
        // - POST /api/ordrer/valider -> validerer ordre
        // - GET /api/kunder/{id} -> henter kunde
        // - GET /api/kunder -> lister alle kunder (ekstra)
        // - POST /api/kunder -> oppretter kunde (ekstra)
    }
}
