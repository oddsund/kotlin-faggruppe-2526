package com.example.ordre.ws2.model

data class ValideringsRespons(
    val gyldig: Boolean,
    val feilmelding: String? = null
)