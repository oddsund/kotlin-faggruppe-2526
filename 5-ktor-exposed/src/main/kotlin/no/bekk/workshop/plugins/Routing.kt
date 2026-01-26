package no.bekk.workshop.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.repository.KundeRepository

/**
 * Konfigurerer Ktor routing med manuell DI (composition root).
 * Dependencies injiseres som parametere.
 */
fun Application.configureRouting(
    ordreValidering: OrdreValidering,
    kundeRepository: KundeRepository
) {
    routing {
        // === Oppgave 1: Health endpoint  ===
        // TODO: Implementer GET /health som returnerer "OK"
        // Tips: Bruk call.respondText()
        

        // === Oppgave 2: Valider ordre ===
        // TODO: Implementer POST /api/ordrer/valider
        // Tips: Motta request, valider, map resultat til riktig HTTP-status
        // Statuskoder: Gyldig->200, TotalForLav->400, KundeIkkeFunnet->404,
        //              KundeInaktiv->400, UtAvLager->409

        // === Oppgave 3: Hent kunde  ===
        // TODO: Implementer GET /api/kunder/{id}
        // Tips: Hent path-parameter med call.parameters["id"]
        // Returner 404 hvis ikke funnet, 400 for ugyldig id

        // === Ekstra: List alle kunder  ===
        // TODO: Implementer GET /api/kunder
        // Tips: Returner liste av KundeDto

        // === Ekstra: Opprett kunde  ===
        // TODO: Implementer POST /api/kunder
        // Tips: Returner 201 Created med Location header
    }
}
