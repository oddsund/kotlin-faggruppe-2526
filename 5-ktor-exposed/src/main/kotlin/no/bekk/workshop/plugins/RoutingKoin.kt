package no.bekk.workshop.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.domain.ValideringsResultat
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import org.koin.ktor.ext.inject

/**
 * Konfigurerer Ktor routing med Koin DI.
 * OrdreValidering hentes fra Koin med inject().
 */
fun Application.configureRoutingKoin() {
    // TODO: Hent OrdreValidering fra Koin med: val ordreValidering by inject<OrdreValidering>()

    routing {
        // TODO: Implementer GET /health som returnerer "OK"

        // TODO: Implementer POST /api/ordrer/valider
        // Samme logikk som i Routing.kt, men bruk inject() i stedet for parameter
    }
}
