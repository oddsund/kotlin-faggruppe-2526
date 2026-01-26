package no.bekk.workshop.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.repository.KundeRepository

/**
 * Konfigurerer Ktor routing med Koin DI.
 * Dependencies hentes fra Koin med inject().
 */
fun Application.configureRoutingKoin() {
    // === Oppgave 5: Inject dependencies fra Koin ===
    // TODO: Hent OrdreValidering og KundeRepository med inject()
    // Tips: val ordreValidering by inject<OrdreValidering>()

    routing {
        // TODO: Implementer samme routes som i Routing.kt
        // Tips: Bruk de injiserte dependencies
    }
}
