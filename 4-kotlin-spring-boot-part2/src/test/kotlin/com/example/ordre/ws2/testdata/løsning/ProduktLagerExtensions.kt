package com.example.ordre.ws2.testdata

import com.example.ordre.ws2.domain.model.løsning.ProduktLager

/**
 * Extension functions på ProduktLager.Companion for å lage testdata enkelt
 *
 * Eksempel bruk:
 *   produktLagerPort.leggTil(ProduktLager.påLager())
 *   produktLagerPort.leggTil(ProduktLager.påLager("P1", 100))
 *   produktLagerPort.leggTil(ProduktLager.utsolgt("P2"))
 */

fun ProduktLager.Companion.påLager(
    produktId: String = "P1",
    antall: Int = 100
) = ProduktLager(
    produktId = produktId,
    antallPåLager = antall
)

fun ProduktLager.Companion.utsolgt(
    produktId: String = "P1"
) = ProduktLager(
    produktId = produktId,
    antallPåLager = 0
)
