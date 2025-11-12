package com.example.ordre.testdata

import com.example.ordre.persistence.entity.KundeEntity

/**
 * Object Mother for JPA entities.
 * MERK: Returnerer entities med id satt (for mocking).
 * I faktiske database-tester vil id være null før persist.
 */
object KundeEntityMother {
    
    fun aktivKunde(
        id: Long = 123,
        navn: String = "Test Kunde"
    ) = KundeEntity(
        id = id,
        navn = navn,
        erAktiv = true
    )
    
    fun inaktivKunde(
        id: Long = 123,
        navn: String = "Inaktiv Kunde"
    ) = KundeEntity(
        id = id,
        navn = navn,
        erAktiv = false
    )
}