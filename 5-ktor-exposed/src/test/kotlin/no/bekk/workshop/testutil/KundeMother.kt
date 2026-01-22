package no.bekk.workshop.testutil

import no.bekk.workshop.domain.Kunde

/**
 * Object Mother pattern for Kunde test-data.
 */
object KundeMother {

    fun aktivKunde(
        id: Long = 123,
        navn: String = "Test Kunde"
    ) = Kunde(
        id = id,
        navn = navn,
        erAktiv = true
    )

    fun inaktivKunde(
        id: Long = 456,
        navn: String = "Inaktiv Kunde"
    ) = Kunde(
        id = id,
        navn = navn,
        erAktiv = false
    )

    fun kundeMedId(id: Long) = aktivKunde(id = id)
}
