package com.example.ordre.ws2.controller

import com.example.ordre.ws2.model.*
import com.example.ordre.ws2.service.OrdreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ordre")
class OrdreController(
    private val ordreService: OrdreService
) {
    
    @PostMapping("/valider")
    fun validerOrdre(@RequestBody request: OrdreRequest): ResponseEntity<ValideringsRespons> {
        val resultat = ordreService.validerOrdre(request)

        return when (resultat) {
            is ValideringsResultat.Gyldig -> {
                ResponseEntity.ok(ValideringsRespons(gyldig = true))
            }
            is ValideringsResultat.Ugyldig.TotalForLav -> {
                ResponseEntity.badRequest().body(
                    ValideringsRespons(
                        gyldig = false,
                        feilmelding = "Ordre total ${resultat.total} kr er under minimum ${resultat.minimum} kr"
                    )
                )
            }
            is ValideringsResultat.Ugyldig.KundeIkkeFunnet -> {
                ResponseEntity.badRequest().body(
                    ValideringsRespons(
                        gyldig = false,
                        feilmelding = "Kunde med ID ${resultat.kundeId} ble ikke funnet"
                    )
                )
            }
            is ValideringsResultat.Ugyldig.KundeInaktiv -> {
                ResponseEntity.badRequest().body(
                    ValideringsRespons(
                        gyldig = false,
                        feilmelding = "Kunde med ID ${resultat.kundeId} er inaktiv"
                    )
                )
            }
            is ValideringsResultat.Ugyldig.UtAvLager -> {
                ResponseEntity.badRequest().body(
                    ValideringsRespons(
                        gyldig = false,
                        feilmelding = "Produkt ${resultat.produktId} er utsolgt"
                    )
                )
            }
        }
    }
}