package no.bekk.workshop.dto

import kotlinx.serialization.Serializable
import no.bekk.workshop.domain.Ordre
import no.bekk.workshop.domain.OrdreVare

@Serializable
data class OrdreRequest(
    val kundeId: Long,
    val varer: List<OrdreVareDto>
) {
    fun tilDomene(): Ordre = Ordre(
        kundeId = kundeId,
        varer = varer.map { it.tilDomene() }
    )
}

@Serializable
data class OrdreVareDto(
    val produktId: String,
    val antall: Int,
    val pris: Double
) {
    fun tilDomene(): OrdreVare = OrdreVare(
        produktId = produktId,
        antall = antall,
        pris = pris
    )
}
