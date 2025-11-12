package com.example.ordre.controller

import com.example.ordre.model.OrdreRequest
import com.example.ordre.model.ValideringsResultat
import com.example.ordre.service.OrdreService
import com.example.ordre.testdata.OrdreMother
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(OrdreController::class)
class OrdreControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @MockkBean
    private lateinit var ordreService: OrdreService
    
    @Test
    fun `skal returnere 200 OK når ordre er gyldig`() {
        // TODO DEL 1 - OPPGAVE 1:
        // 1. Lag OrdreRequest med total over 100
        // 2. Stub ordreService.validerOrdre til å returnere ValideringsResultat.Gyldig
        // 3. Post til /api/ordrer/valider
        // 4. Assert status 200 OK og gyldig = true
        
        throw NotImplementedError("Implementeres i Del 1 - Oppgave 1")
    }
    
    @Test
    fun `skal returnere 400 når ordre total er under minimum`() {
        // TODO DEL 1 - OPPGAVE 1:
        // 1. Lag OrdreRequest med total under 100
        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.TotalForLav(50.0, 100.0)
        // 3. Post til /api/ordrer/valider
        // 4. Assert status 400 og feilmelding inneholder "total" og beløp
        
        throw NotImplementedError("Implementeres i Del 1 - Oppgave 1")
    }
    
    @Test
    fun `skal returnere 400 når produkt er utsolgt`() {
        // TODO DEL 1 - OPPGAVE 2:
        // 1. Lag OrdreRequest
        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.UtAvLager("P1")
        // 3. Assert status 400 og feilmelding inneholder "utsolgt" eller "lager" og produktId
        
        throw NotImplementedError("Implementeres i Del 1 - Oppgave 2")
    }
    
    @Test
    fun `skal returnere 400 når kunde er inaktiv`() {
        // TODO DEL 1 - OPPGAVE 3:
        // 1. Lag OrdreRequest med kundeId = 999
        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.KundeInaktiv(999)
        // 3. Assert status 400 og feilmelding inneholder "inaktiv" og kundeId
        
        throw NotImplementedError("Implementeres i Del 1 - Oppgave 3")
    }
    
    @Test
    fun `skal kalle ordreService med korrekte request-data`() {
        // TODO DEL 1 - OPPGAVE 4:
        // 1. Bruk slot<OrdreRequest>() for å capture argumentet
        // 2. Stub ordreService med capture(slot)
        // 3. Post request med kundeId=123 og 2 varer
        // 4. Verifiser at ordreService.validerOrdre ble kalt exactly 1 gang
        // 5. Verifiser slot.captured.kundeId == 123 og har 2 varer
        
        throw NotImplementedError("Implementeres i Del 1 - Oppgave 4")
    }
}