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

/**
 * Konfigurerer Ktor routing med manuell DI (composition root).
 * OrdreValidering injiseres som parameter.
 */
fun Application.configureRouting(ordreValidering: OrdreValidering) {
    routing {
        // TODO: Implementer GET /health som returnerer "OK"

        // TODO: Implementer POST /api/ordrer/valider
        // 1. Motta OrdreRequest med call.receive<OrdreRequest>()
        // 2. Konverter til domene med request.tilDomene()
        // 3. Kall ordreValidering.valider(ordre)
        // 4. Map ValideringsResultat til HTTP status og ValideringsRespons:
        //    - Gyldig -> 200 OK
        //    - TotalForLav -> 400 Bad Request
        //    - KundeIkkeFunnet -> 404 Not Found
        //    - KundeInaktiv -> 400 Bad Request
        //    - UtAvLager -> 409 Conflict
        // 5. Bruk call.respond(HttpStatusCode.X, ValideringsRespons(...))
    }
}
