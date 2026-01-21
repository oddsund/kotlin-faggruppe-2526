package com.example.ordre.model

data class ValideringsRespons(
    val gyldig: Boolean,
    val feilmelding: String? = null
)