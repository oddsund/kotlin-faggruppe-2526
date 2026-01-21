package com.example.ordre.ws2.testdata

import com.example.ordre.ws2.domain.model.løsning.Kunde

/**
 * Extension functions på Kunde.Companion for å lage testdata enkelt
 *
 * Eksempel bruk:
 *   kundePort.leggTil(Kunde.gyldig())
 *   kundePort.leggTil(Kunde.inaktiv())
 */

/**
 * Lager en gyldig, aktiv kunde for testing
 */
fun Kunde.Companion.gyldig(
    id: Long = 123,
    navn: String = "Test Kunde",
    erAktiv: Boolean = true
) = Kunde(
    id = id,
    navn = navn,
    erAktiv = erAktiv
)

/**
 * Lager en inaktiv kunde for testing
 */
fun Kunde.Companion.inaktiv(
    id: Long = 123,
    navn: String = "Inaktiv Kunde"
) = Kunde(
    id = id,
    navn = navn,
    erAktiv = false
)
