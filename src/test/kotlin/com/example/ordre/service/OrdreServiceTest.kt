package com.example.ordre.service

import com.example.ordre.model.ValideringsResultat
import com.example.ordre.persistence.repository.KundeRepository
import com.example.ordre.persistence.repository.ProduktLagerRepository
import com.example.ordre.testdata.KundeEntityMother
import com.example.ordre.testdata.OrdreMother
import com.example.ordre.testdata.ProduktLagerEntityMother
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
        // 1. Lag ordre med total under 100 (bruk OrdreMother.ordreMedTotal(50.0))
        val ordre = OrdreMother.ordreMedTotal(50.0)

        // 2. Kall ordreService.validerOrdre (IKKE stub repository!)
        val resultat = ordreService.validerOrdre(ordre)

        // 3. Assert at resultatet er ValideringsResultat.Ugyldig.TotalForLav
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.TotalForLav>()

        // 4. Assert at resultat.total == 50.0 og resultat.minimum == 100.0
        resultat.total shouldBe 50.0
        resultat.minimum shouldBe 100.0

        // 5. Verifiser at kundeRepository IKKE ble kalt (early return)
        verify(exactly = 0) { kundeRepository.findById(any()) }
    }
    
    @Test
    fun `skal returnere KundeIkkeFunnet når kunde ikke eksisterer`() {
        // 1. Lag gyldig ordre (over minimum total)
        val ordre = OrdreMother.gyldigOrdre()

        // 2. Stub kundeRepository.findById til å returnere Optional.empty()
        every { kundeRepository.findById(any()) } returns Optional.empty()

        // 3. Kall ordreService.validerOrdre
        val resultat = ordreService.validerOrdre(ordre)

        // 4. Assert ValideringsResultat.Ugyldig.KundeIkkeFunnet
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeIkkeFunnet>()

        // 5. Assert at kundeId i resultat matcher request
        resultat.kundeId shouldBe ordre.kundeId
    }
    
    @Test
    fun `skal returnere KundeInaktiv når kunde ikke er aktiv`() {
        // 1. Lag gyldig ordre
        val ordre = OrdreMother.gyldigOrdre()

        // 2. Stub kundeRepository.findById til å returnere Optional.of(inaktivKunde)
        val inaktivKunde = KundeEntityMother.inaktivKunde()
        every { kundeRepository.findById(any()) } returns Optional.of(inaktivKunde)

        // 3. Assert ValideringsResultat.Ugyldig.KundeInaktiv med riktig ID
        val resultat = ordreService.validerOrdre(ordre)
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeInaktiv>()
        resultat.kundeId shouldBe ordre.kundeId
    }
    
    @Test
    fun `skal returnere UtAvLager når produkt er utilgjengelig`() {
        // 1. Lag ordre med 2 varer ("P1" og "P2")
        val ordre = OrdreMother.ordreMedVarer("P1", "P2")

        // 2. Stub kundeRepository med aktivKunde wrappet i Optional.of()
        val aktivKunde = KundeEntityMother.aktivKunde()
        every { kundeRepository.findById(any()) } returns Optional.of(aktivKunde)

        // 3. Stub produktLagerRepository:
        val påLagerEntity = ProduktLagerEntityMother.påLager("P1")
        every { produktLagerRepository.findByProduktId("P1") } returns påLagerEntity
        every { produktLagerRepository.findByProduktId("P2") } returns null

        // 4. Assert ValideringsResultat.Ugyldig.UtAvLager med produktId = "P2"
        val resultat = ordreService.validerOrdre(ordre)
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.UtAvLager>()
        resultat.produktId shouldBe "P2"
    }
    
    @Test
    fun `skal returnere Gyldig når alle sjekker passerer`() {
        // 1. Lag gyldig ordre med 3 varer
        val ordre = OrdreMother.ordreMedVarer("P1", "P2", "P3")

        // 2. Stub kundeRepository med Optional.of(aktivKunde)
        val aktivKunde = KundeEntityMother.aktivKunde()
        every { kundeRepository.findById(any()) } returns Optional.of(aktivKunde)

        // 3. Stub produktLagerRepository for alle 3 produktIds med påLager entities
        every { produktLagerRepository.findByProduktId("P1") } returns ProduktLagerEntityMother.påLager("P1")
        every { produktLagerRepository.findByProduktId("P2") } returns ProduktLagerEntityMother.påLager("P2")
        every { produktLagerRepository.findByProduktId("P3") } returns ProduktLagerEntityMother.påLager("P3")

        // 4. Kall ordreService.validerOrdre
        val resultat = ordreService.validerOrdre(ordre)

        // 5. Assert Gyldig
        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()

        // 6. Tell opp hvor mange linjer kode du trengte for stubbing!
        // Svar: 5 linjer (1 for kunde + 3 for produkter + 1 for aktivKunde variabel)
    }
}