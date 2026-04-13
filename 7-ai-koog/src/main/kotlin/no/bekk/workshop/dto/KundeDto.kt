package no.bekk.workshop.dto

import kotlinx.serialization.Serializable
import no.bekk.workshop.domain.Kunde

@Serializable
data class KundeDto(
    val id: Long,
    val navn: String,
    val erAktiv: Boolean
) {
    companion object {
        fun fraKunde(kunde: Kunde) = KundeDto(
            id = kunde.id,
            navn = kunde.navn,
            erAktiv = kunde.erAktiv
        )
    }
}

@Serializable
data class OpprettKundeRequest(
    val navn: String,
    val erAktiv: Boolean = true
) {
    fun tilKunde(id: Long = 0) = Kunde(
        id = id,
        navn = navn,
        erAktiv = erAktiv
    )
}
