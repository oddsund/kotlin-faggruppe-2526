package com.example.ordre.model

data class OrdreRequest(
    val kundeId: Long,
    val varer: List<OrdreVare>
) {
    fun totalBeløp(): Double = varer.sumOf { it.totalPris() }
}