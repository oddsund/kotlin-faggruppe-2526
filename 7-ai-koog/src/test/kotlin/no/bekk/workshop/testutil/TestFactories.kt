package no.bekk.workshop.testutil

import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.domain.Ordre
import no.bekk.workshop.domain.OrdreVare
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.OrdreVareDto

/**
 * Test Factory functions for domene-objekter.
 * Brukes som extension functions på companion objects.
 *
 * Eksempel: Kunde.gyldig(id = 1, navn = "Test")
 */

// === Kunde factories ===

fun Kunde.Companion.gyldig(
    id: Long = 1,
    navn: String = "Test Kunde",
    erAktiv: Boolean = true
) = Kunde(id = id, navn = navn, erAktiv = erAktiv)

fun Kunde.Companion.inaktiv(id: Long = 1, navn: String = "Inaktiv Kunde") =
    Kunde.gyldig(id = id, navn = navn, erAktiv = false)

// === Ordre factories ===

fun Ordre.Companion.gyldig(
    kundeId: Long = 1,
    produkter: List<String> = listOf("P1"),
    total: Double = 200.0
): Ordre {
    val prisPrVare = total / produkter.size
    return Ordre(
        kundeId = kundeId,
        varer = produkter.map { OrdreVare.gyldig(produktId = it, pris = prisPrVare) }
    )
}

fun Ordre.Companion.underMinimum(kundeId: Long = 1) =
    Ordre.gyldig(kundeId = kundeId, total = 50.0)

fun Ordre.Companion.medProdukter(vararg produktIds: String, kundeId: Long = 1) =
    Ordre.gyldig(kundeId = kundeId, produkter = produktIds.toList())

// === OrdreVare factories ===

fun OrdreVare.Companion.gyldig(
    produktId: String = "P1",
    antall: Int = 1,
    pris: Double = 100.0
) = OrdreVare(produktId = produktId, antall = antall, pris = pris)

// === DTO factories for HTTP-tester ===

fun OrdreRequest.Companion.gyldig(
    kundeId: Long = 1,
    produkter: List<String> = listOf("P1"),
    total: Double = 200.0
): OrdreRequest {
    val prisPrVare = total / produkter.size
    return OrdreRequest(
        kundeId = kundeId,
        varer = produkter.map { OrdreVareDto(produktId = it, antall = 1, pris = prisPrVare) }
    )
}

fun OrdreRequest.Companion.underMinimum(kundeId: Long = 1) =
    OrdreRequest.gyldig(kundeId = kundeId, total = 50.0)

fun OrdreRequest.Companion.medProdukter(vararg produktIds: String, kundeId: Long = 1) =
    OrdreRequest.gyldig(kundeId = kundeId, produkter = produktIds.toList())
