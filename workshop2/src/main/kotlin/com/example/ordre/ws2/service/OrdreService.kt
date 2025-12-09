package com.example.ordre.ws2.service

import com.example.ordre.ws2.model.OrdreRequest
import com.example.ordre.ws2.model.ValideringsResultat
import com.example.ordre.ws2.persistence.repository.KundeRepository
import com.example.ordre.ws2.persistence.repository.ProduktLagerRepository
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
        val total = request.totalBeløp()
        if (total < MINIMUM_ORDRE_TOTAL) {
            return ValideringsResultat.Ugyldig.TotalForLav(total, MINIMUM_ORDRE_TOTAL)
        }

        val kundeOptional = kundeRepository.findById(request.kundeId)

        if (kundeOptional.isEmpty) {
            return ValideringsResultat.Ugyldig.KundeIkkeFunnet(request.kundeId)
        }

        val kunde = kundeOptional.get()

        if (!kunde.erAktiv) {
            return ValideringsResultat.Ugyldig.KundeInaktiv(request.kundeId)
        }

        for (vare in request.varer) {
            val lagerStatus = produktLagerRepository.findByProduktId(vare.produktId)

            if (lagerStatus == null || lagerStatus.antallPåLager <= 0) {
                return ValideringsResultat.Ugyldig.UtAvLager(vare.produktId)
            }
        }

        return ValideringsResultat.Gyldig
    }
}