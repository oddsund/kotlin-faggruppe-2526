package com.example.ordre.ws2.controller

import com.example.ordre.ws2.model.OrdreRequest
import com.example.ordre.ws2.model.ValideringsResultat
import com.example.ordre.ws2.service.OrdreService
import com.example.ordre.ws2.testdata.OrdreMother
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
        val request = OrdreMother.ordreMedTotal(150.0)

        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Gyldig

        mockMvc.post("/api/ordre/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.gyldig") { value(true) }
        }
    }
    
    @Test
    fun `skal returnere 400 når ordre total er under minimum`() {
        val request = OrdreMother.ordreMedTotal(50.0)

        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.TotalForLav(50.0, 100.0)

        mockMvc.post("/api/ordre/valider") {
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
        val request = OrdreMother.ordreMedVarer("P1")

        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.UtAvLager("P1")

        mockMvc.post("/api/ordre/valider") {
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
        val request = OrdreMother.ordreMedKunde(999)

        every { ordreService.validerOrdre(any()) } returns ValideringsResultat.Ugyldig.KundeInaktiv(999)

        mockMvc.post("/api/ordre/valider") {
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
        val slot = slot<OrdreRequest>()

        every { ordreService.validerOrdre(capture(slot)) } returns ValideringsResultat.Gyldig

        val request = OrdreMother.ordreMedVarer("P1", "P2")

        mockMvc.post("/api/ordre/valider") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        verify(exactly = 1) { ordreService.validerOrdre(any()) }

        slot.captured.kundeId shouldBe 123
        slot.captured.varer.size shouldBe 2
    }
}