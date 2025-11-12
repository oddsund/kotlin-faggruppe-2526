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
        // 1. Sjekk at total er over minimum
        val total = request.totalBeløp()
        if (total < MINIMUM_ORDRE_TOTAL) {
            return ValideringsResultat.Ugyldig.TotalForLav(total, MINIMUM_ORDRE_TOTAL)
        }

        // 2. Hent kunde fra kundeRepository (håndter Optional!)
        val kundeOptional = kundeRepository.findById(request.kundeId)

        // 3. Sjekk at kunde eksisterer
        if (kundeOptional.isEmpty) {
            return ValideringsResultat.Ugyldig.KundeIkkeFunnet(request.kundeId)
        }

        val kunde = kundeOptional.get()

        // 4. Sjekk at kunde er aktiv
        if (!kunde.erAktiv) {
            return ValideringsResultat.Ugyldig.KundeInaktiv(request.kundeId)
        }

        // 5. For hver vare: sjekk at det er nok på lager
        for (vare in request.varer) {
            val lagerStatus = produktLagerRepository.findByProduktId(vare.produktId)

            if (lagerStatus == null || lagerStatus.antallPåLager <= 0) {
                return ValideringsResultat.Ugyldig.UtAvLager(vare.produktId)
            }
        }

        return ValideringsResultat.Gyldig
    }
}