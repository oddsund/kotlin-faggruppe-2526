package com.example.ordre.service

import com.example.ordre.model.OrdreRequest
import com.example.ordre.model.ValideringsResultat
import com.example.ordre.persistence.repository.KundeRepository
import com.example.ordre.persistence.repository.ProduktLagerRepository
import org.springframework.stereotype.Service

@Service
class OrdreService(
    private val kundeRepository: KundeRepository,
    private val produktLagerRepository: ProduktLagerRepository
) {
    
    companion object {
        private const val MINIMUM_ORDRE_TOTAL = 100.0
    }
    
    fun validerOrdre(request: OrdreRequest): ValideringsResultat {
        // TODO DEL 2:
        // 1. Sjekk at total er over minimum (allerede implementert under)
        // 2. Hent kunde fra kundeRepository (håndter Optional!)
        // 3. Sjekk at kunde eksisterer
        // 4. Sjekk at kunde er aktiv
        // 5. For hver vare: sjekk at det er nok på lager
        
        // Implementert: Total-sjekk
        val total = request.totalBeløp()
        if (total < MINIMUM_ORDRE_TOTAL) {
            return ValideringsResultat.Ugyldig.TotalForLav(total, MINIMUM_ORDRE_TOTAL)
        }
        
        // TODO: Implementer resten her
        
        return ValideringsResultat.Gyldig
    }
}