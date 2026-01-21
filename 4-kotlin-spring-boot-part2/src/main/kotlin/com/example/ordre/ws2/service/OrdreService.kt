package com.example.ordre.ws2.service.løsning

import com.example.ordre.ws2.domain.port.løsning.KundePort
import com.example.ordre.ws2.domain.port.løsning.ProduktLagerPort
import com.example.ordre.ws2.model.OrdreRequest
import com.example.ordre.ws2.model.ValideringsResultat
import org.springframework.stereotype.Service

// LØSNING: Refaktorert til å bruke ports i stedet for repositories
//
// Endringer fra original:
// 1. KundeRepository -> KundePort (interface, ikke konkret repository)
// 2. ProduktLagerRepository -> ProduktLagerPort
// 3. Fjernet Optional-håndtering (ports returnerer nullable)
// 4. Domain layer er nå uavhengig av persistence layer (JPA)

@Service
class OrdreService(
    private val kundePort: KundePort,
    private val produktLagerPort: ProduktLagerPort
) {

    companion object {
        private const val MINIMUM_ORDRE_TOTAL = 100.0
    }

    fun validerOrdre(request: OrdreRequest): ValideringsResultat {
        val total = request.totalBeløp()
        if (total < MINIMUM_ORDRE_TOTAL) {
            return ValideringsResultat.Ugyldig.TotalForLav(total, MINIMUM_ORDRE_TOTAL)
        }

        val kunde = kundePort.hentKunde(request.kundeId)

        if (kunde == null) {
            return ValideringsResultat.Ugyldig.KundeIkkeFunnet(request.kundeId)
        }

        if (!kunde.erAktiv) {
            return ValideringsResultat.Ugyldig.KundeInaktiv(request.kundeId)
        }

        for (vare in request.varer) {
            val lagerStatus = produktLagerPort.hentLagerStatus(vare.produktId)

            if (lagerStatus == null || lagerStatus.antallPåLager <= 0) {
                return ValideringsResultat.Ugyldig.UtAvLager(vare.produktId)
            }
        }

        return ValideringsResultat.Gyldig
    }
}

