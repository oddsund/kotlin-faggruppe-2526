package com.example.ordre.controller

import com.example.ordre.model.*
import com.example.ordre.service.OrdreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ordrer")
class OrdreController(
    private val ordreService: OrdreService
) {
    
    @PostMapping("/valider")
    fun validerOrdre(@RequestBody request: OrdreRequest): ResponseEntity<ValideringsRespons> {
        // 1. Kall ordreService.validerOrdre(request)
        val resultat = ordreService.validerOrdre(request)

        // 2. Map ValideringsResultat til HTTP status og ValideringsRespons
        // 3. Bruk when expression for exhaustive sealed class handling
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