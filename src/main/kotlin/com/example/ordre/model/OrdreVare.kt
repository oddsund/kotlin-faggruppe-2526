package com.example.ordre.model

data class OrdreVare(
    val produktId: String,
    val antall: Int,
    val pris: Double
) {
    fun totalPris(): Double = antall * pris
}