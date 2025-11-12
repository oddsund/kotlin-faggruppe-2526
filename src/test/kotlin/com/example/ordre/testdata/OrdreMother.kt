package com.example.ordre.testdata

import com.example.ordre.model.OrdreRequest
import com.example.ordre.model.OrdreVare

/**
 * Object Mother pattern for å lage test-data.
 * Gir standard verdier som kan overrides ved behov.
 */
object OrdreMother {
    
    fun gyldigOrdre(
        kundeId: Long = 123,
        total: Double = 120.0
    ): OrdreRequest {
        val antalVarer = 2
        val prisPrVare = total / antalVarer
        
        return OrdreRequest(
            kundeId = kundeId,
            varer = listOf(
                OrdreVare(
                    produktId = "P1",
                    antall = antalVarer,
                    pris = prisPrVare
                )
            )
        )
    }
    
    fun ordreMedTotal(total: Double) = gyldigOrdre(total = total)
    
    fun ordreMedKunde(kundeId: Long) = gyldigOrdre(kundeId = kundeId)
    
    fun ordreMedVarer(vararg produktIds: String): OrdreRequest {
        return OrdreRequest(
            kundeId = 123,
            varer = produktIds.map { 
                OrdreVare(produktId = it, antall = 2, pris = 60.0) 
            }
        )
    }
}