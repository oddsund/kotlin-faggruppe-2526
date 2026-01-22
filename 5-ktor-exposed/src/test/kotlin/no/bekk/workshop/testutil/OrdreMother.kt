package no.bekk.workshop.testutil

import no.bekk.workshop.domain.Ordre
import no.bekk.workshop.domain.OrdreVare
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.OrdreVareDto

/**
 * Object Mother pattern for Ordre test-data.
 */
object OrdreMother {

    fun gyldigOrdre(
        kundeId: Long = 123,
        total: Double = 120.0
    ): Ordre {
        val antallVarer = 2
        val prisPrVare = total / antallVarer

        return Ordre(
            kundeId = kundeId,
            varer = listOf(
                OrdreVare(
                    produktId = "P1",
                    antall = antallVarer,
                    pris = prisPrVare
                )
            )
        )
    }

    fun ordreMedTotal(total: Double) = gyldigOrdre(total = total)

    fun ordreMedKunde(kundeId: Long) = gyldigOrdre(kundeId = kundeId)

    fun ordreMedVarer(vararg produktIds: String): Ordre {
        return Ordre(
            kundeId = 123,
            varer = produktIds.map {
                OrdreVare(produktId = it, antall = 2, pris = 60.0)
            }
        )
    }

    // DTO-versjoner for HTTP-tester
    fun gyldigOrdreRequest(
        kundeId: Long = 123,
        total: Double = 120.0
    ): OrdreRequest {
        val antallVarer = 2
        val prisPrVare = total / antallVarer

        return OrdreRequest(
            kundeId = kundeId,
            varer = listOf(
                OrdreVareDto(
                    produktId = "P1",
                    antall = antallVarer,
                    pris = prisPrVare
                )
            )
        )
    }

    fun ordreRequestMedTotal(total: Double) = gyldigOrdreRequest(total = total)

    fun ordreRequestMedKunde(kundeId: Long) = gyldigOrdreRequest(kundeId = kundeId)

    fun ordreRequestMedVarer(vararg produktIds: String): OrdreRequest {
        return OrdreRequest(
            kundeId = 123,
            varer = produktIds.map {
                OrdreVareDto(produktId = it, antall = 2, pris = 60.0)
            }
        )
    }
}
