package com.example.ordre.testdata

import com.example.ordre.persistence.entity.ProduktLagerEntity

object ProduktLagerEntityMother {
    
    fun påLager(
        produktId: String = "P1",
        antall: Int = 100
    ) = ProduktLagerEntity(
        produktId = produktId,
        antallPåLager = antall
    )
    
    fun utsolgt(produktId: String = "P1") = ProduktLagerEntity(
        produktId = produktId,
        antallPåLager = 0
    )
}