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
        // TODO DEL 1:
        // 1. Kall ordreService.validerOrdre(request)
        // 2. Map ValideringsResultat til HTTP status og ValideringsRespons
        // 3. Bruk when expression for exhaustive sealed class handling
        
        throw NotImplementedError("Implementeres i Del 1")
    }
}