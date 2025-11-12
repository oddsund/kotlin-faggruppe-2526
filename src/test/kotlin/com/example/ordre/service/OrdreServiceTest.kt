package com.example.ordre.service

import com.example.ordre.model.ValideringsResultat
import com.example.ordre.persistence.repository.KundeRepository
import com.example.ordre.persistence.repository.ProduktLagerRepository
import com.example.ordre.testdata.KundeEntityMother
import com.example.ordre.testdata.OrdreMother
import com.example.ordre.testdata.ProduktLagerEntityMother
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*

class OrdreServiceTest {
    
    private val kundeRepository: KundeRepository = mockk()
    private val produktLagerRepository: ProduktLagerRepository = mockk()
    
    private val ordreService = OrdreService(kundeRepository, produktLagerRepository)
    
    @Test
    fun `skal returnere TotalForLav når ordre er under minimum`() {
        // TODO DEL 2 - OPPGAVE 1:
        // 1. Lag ordre med total under 100 (bruk OrdreMother.ordreMedTotal(50.0))
        // 2. Kall ordreService.validerOrdre (IKKE stub repository!)
        // 3. Assert at resultatet er ValideringsResultat.Ugyldig.TotalForLav
        // 4. Assert at resultat.total == 50.0 og resultat.minimum == 100.0
        // 5. Verifiser at kundeRepository IKKE ble kalt (early return)
        //    Hint: verify(exactly = 0) { kundeRepository.findById(any()) }
        
        throw NotImplementedError("Implementeres i Del 2 - Oppgave 1")
    }
    
    @Test
    fun `skal returnere KundeIkkeFunnet når kunde ikke eksisterer`() {
        // TODO DEL 2 - OPPGAVE 2:
        // 1. Lag gyldig ordre (over minimum total)
        // 2. Stub kundeRepository.findById til å returnere Optional.empty()
        //    Hint: every { kundeRepository.findById(any()) } returns Optional.empty()
        // 3. Kall ordreService.validerOrdre
        // 4. Assert ValideringsResultat.Ugyldig.KundeIkkeFunnet
        // 5. Assert at kundeId i resultat matcher request
        
        throw NotImplementedError("Implementeres i Del 2 - Oppgave 2")
    }
    
    @Test
    fun `skal returnere KundeInaktiv når kunde ikke er aktiv`() {
        // TODO DEL 2 - OPPGAVE 3:
        // 1. Lag gyldig ordre
        // 2. Stub kundeRepository.findById til å returnere Optional.of(inaktivKunde)
        //    Hint: bruk KundeEntityMother.inaktivKunde()
        //    every { kundeRepository.findById(any()) } returns Optional.of(kunde)
        // 3. Assert ValideringsResultat.Ugyldig.KundeInaktiv med riktig ID
        
        throw NotImplementedError("Implementeres i Del 2 - Oppgave 3")
    }
    
    @Test
    fun `skal returnere UtAvLager når produkt er utilgjengelig`() {
        // TODO DEL 2 - OPPGAVE 4:
        // 1. Lag ordre med 2 varer ("P1" og "P2")
        //    Hint: OrdreMother.ordreMedVarer("P1", "P2")
        // 2. Stub kundeRepository med aktivKunde wrappet i Optional.of()
        // 3. Stub produktLagerRepository:
        //    every { produktLagerRepository.findByProduktId("P1") } returns påLagerEntity
        //    every { produktLagerRepository.findByProduktId("P2") } returns null
        // 4. Assert ValideringsResultat.Ugyldig.UtAvLager med produktId = "P2"
        
        throw NotImplementedError("Implementeres i Del 2 - Oppgave 4")
    }
    
    @Test
    fun `skal returnere Gyldig når alle sjekker passerer`() {
        // TODO DEL 2 - OPPGAVE 5:
        // 1. Lag gyldig ordre med 3 varer
        // 2. Stub kundeRepository med Optional.of(aktivKunde)
        // 3. Stub produktLagerRepository for alle 3 produktIds med påLager entities
        // 4. Kall ordreService.validerOrdre
        // 5. Assert Gyldig
        // 6. Tell opp hvor mange linjer kode du trengte for stubbing!
        
        throw NotImplementedError("Implementeres i Del 2 - Oppgave 5")
    }
}