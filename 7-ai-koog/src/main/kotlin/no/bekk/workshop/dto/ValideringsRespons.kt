package no.bekk.workshop.dto

import kotlinx.serialization.Serializable

@Serializable
data class ValideringsRespons(
    val gyldig: Boolean,
    val feilmelding: String? = null
)
