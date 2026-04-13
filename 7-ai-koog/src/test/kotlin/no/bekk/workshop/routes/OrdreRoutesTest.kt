package no.bekk.workshop.routes

import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRouting
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrdreRoutesTest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
    }

    private fun ApplicationTestBuilder.setupApp() {
        val ordreValidering = OrdreValidering(kundeRepository, lagerRepository)
        application {
            configureSerialization()
            configureRouting(ordreValidering, kundeRepository)
        }
    }

    @Test
    fun `GET health returnerer OK`() = testApplication {
        setupApp()

        val response = client.get("/health")

        response.status shouldBe HttpStatusCode.OK
        response.body<String>() shouldBe "OK"
    }

    @Test
    fun `POST ordre-valider returnerer 200 for gyldig ordre`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        lagerRepository.settBeholdning("P1", 10)

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        response.status shouldBe HttpStatusCode.OK
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe true
    }

    @Test
    fun `POST ordre-valider returnerer 400 for ordre under minimum`() = testApplication {
        setupApp()

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.underMinimum())
        }

        response.status shouldBe HttpStatusCode.BadRequest
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
    }

    @Test
    fun `POST ordre-valider returnerer 404 med KUNDE_IKKE_FUNNET`() = testApplication {
        setupApp()
        // Ingen kunde lagt til

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 999))
        }

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `POST ordre-valider returnerer 400 med KUNDE_INAKTIV`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.inaktiv(id = 1))

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `POST ordre-valider returnerer 409 med UT_AV_LAGER`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        // Ingen lagerbeholdning

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        response.status shouldBe HttpStatusCode.Conflict
    }
}
