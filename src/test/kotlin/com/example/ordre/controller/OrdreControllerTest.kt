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
        // 1. Lag OrdreRequest med total over 100
        val request = OrdreMother.ordreMedTotal(150.0)

        // 2. Stub ordreService.validerOrdre til å returnere ValideringsResultat.Gyldig
        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Gyldig

        // 3. Post til /api/ordrer/valider
        mockMvc.post("/api/ordrer/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
        // 4. Assert status 200 OK og gyldig = true
            status { isOk() }
            jsonPath("$.gyldig") { value(true) }
        }
    }
    
    @Test
    fun `skal returnere 400 når ordre total er under minimum`() {
        // 1. Lag OrdreRequest med total under 100
        val request = OrdreMother.ordreMedTotal(50.0)

        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.TotalForLav(50.0, 100.0)
        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.TotalForLav(50.0, 100.0)

        // 3. Post til /api/ordrer/valider
        // 4. Assert status 400 og feilmelding inneholder "total" og beløp
        mockMvc.post("/api/ordrer/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.gyldig") { value(false) }
            jsonPath("$.feilmelding") { value("Ordre total 50.0 kr er under minimum 100.0 kr") }

            // Alternativt: Sammenlign hele responsen som JSON string
            // content { json("""{"gyldig":false,"feilmelding":"Ordre total 50.0 kr er under minimum 100.0 kr"}""") }
        }
    }
    
    @Test
    fun `skal returnere 400 når produkt er utsolgt`() {
        // 1. Lag OrdreRequest
        val request = OrdreMother.ordreMedVarer("P1")

        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.UtAvLager("P1")
        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.UtAvLager("P1")

        // 3. Assert status 400 og feilmelding inneholder "utsolgt" eller "lager" og produktId
        mockMvc.post("/api/ordrer/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.gyldig") { value(false) }
            jsonPath("$.feilmelding") { value("Produkt P1 er utsolgt") }
        }
    }
    
    @Test
    fun `skal returnere 400 når kunde er inaktiv`() {
        // 1. Lag OrdreRequest med kundeId = 999
        val request = OrdreMother.ordreMedKunde(999)

        // 2. Stub ordreService til å returnere ValideringsResultat.Ugyldig.KundeInaktiv(999)
        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.KundeInaktiv(999)

        // 3. Assert status 400 og feilmelding inneholder "inaktiv" og kundeId
        mockMvc.post("/api/ordrer/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.gyldig") { value(false) }
            jsonPath("$.feilmelding") { value("Kunde med ID 999 er inaktiv") }
        }
    }
    
    @Test
    fun `skal kalle ordreService med korrekte request-data`() {
        // 1. Bruk slot<OrdreRequest>() for å capture argumentet
        val slot = slot<OrdreRequest>()

        // 2. Stub ordreService med capture(slot)
        every { ordreService.validerOrdre(capture(slot)) } returns ValideringsResultat.Gyldig

        // 3. Post request med kundeId=123 og 2 varer
        val request = OrdreMother.ordreMedVarer("P1", "P2")

        mockMvc.post("/api/ordrer/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        // 4. Verifiser at ordreService.validerOrdre ble kalt exactly 1 gang
        verify(exactly = 1) { ordreService.validerOrdre(any()) }

        // 5. Verifiser slot.captured.kundeId == 123 og har 2 varer
        slot.captured.kundeId shouldBe 123
        slot.captured.varer.size shouldBe 2
    }
}