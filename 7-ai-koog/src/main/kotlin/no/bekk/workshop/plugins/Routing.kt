package no.bekk.workshop.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.domain.ValideringsResultat
import no.bekk.workshop.dto.KundeDto
import no.bekk.workshop.dto.OpprettKundeRequest
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.repository.KundeRepository

fun Application.configureRouting(
    ordreValidering: OrdreValidering,
    kundeRepository: KundeRepository
) {
    routing {
        get("/health") {
            call.respondText("OK")
        }

        post("/api/ordrer/valider") {
            val request = call.receive<OrdreRequest>()
            val ordre = request.tilDomene()

            when (val resultat = ordreValidering.valider(ordre)) {
                is ValideringsResultat.Gyldig -> {
                    call.respond(HttpStatusCode.OK, ValideringsRespons(gyldig = true))
                }
                is ValideringsResultat.Ugyldig.TotalForLav -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ValideringsRespons(
                            gyldig = false,
                            feilmelding = "Ordre total ${resultat.total} er under minimum ${resultat.minimum}"
                        )
                    )
                }
                is ValideringsResultat.Ugyldig.KundeIkkeFunnet -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ValideringsRespons(
                            gyldig = false,
                            feilmelding = "Kunde ${resultat.kundeId} finnes ikke"
                        )
                    )
                }
                is ValideringsResultat.Ugyldig.KundeInaktiv -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ValideringsRespons(
                            gyldig = false,
                            feilmelding = "Kunde ${resultat.kundeId} er inaktiv"
                        )
                    )
                }
                is ValideringsResultat.Ugyldig.UtAvLager -> {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ValideringsRespons(
                            gyldig = false,
                            feilmelding = "Produkt ${resultat.produktId} er utsolgt"
                        )
                    )
                }
            }
        }

        get("/api/kunder/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ugyldig kunde-id")
                return@get
            }

            val kunde = kundeRepository.hent(id)
            if (kunde == null) {
                call.respond(HttpStatusCode.NotFound, "Kunde ikke funnet")
                return@get
            }

            call.respond(KundeDto.fraKunde(kunde))
        }

        get("/api/kunder") {
            val kunder = kundeRepository.hentAlle()
            call.respond(kunder.map { KundeDto.fraKunde(it) })
        }

        post("/api/kunder") {
            val request = call.receive<OpprettKundeRequest>()
            val kunde = request.tilKunde()
            val id = kundeRepository.lagre(kunde)

            call.response.header(HttpHeaders.Location, "/api/kunder/$id")
            call.respond(HttpStatusCode.Created, KundeDto.fraKunde(kunde.copy(id = id)))
        }
    }
}
