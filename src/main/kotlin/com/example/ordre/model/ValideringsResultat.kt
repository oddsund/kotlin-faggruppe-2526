package com.example.ordre.model

sealed class ValideringsResultat {
    object Gyldig : ValideringsResultat()
    
    sealed class Ugyldig : ValideringsResultat() {
        data class TotalForLav(val total: Double, val minimum: Double) : Ugyldig()
        data class KundeIkkeFunnet(val kundeId: Long) : Ugyldig()
        data class KundeInaktiv(val kundeId: Long) : Ugyldig()
        data class UtAvLager(val produktId: String) : Ugyldig()
    }
}