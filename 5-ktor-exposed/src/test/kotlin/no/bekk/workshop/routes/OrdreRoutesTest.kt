package no.bekk.workshop.routes

import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
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

    private fun ApplicationTestBuilder.setupApp(): OrdreValidering {
        val ordreValidering = OrdreValidering(kundeRepository, lagerRepository)
        application {
            configureSerialization()
            configureRouting(ordreValidering)
        }
        return ordreValidering
    }

    @Test
    fun `health endpoint returnerer OK`() = testApplication {
        setupApp()

        val response = client.get("/health")

        response.status shouldBe HttpStatusCode.OK
        response.body<String>() shouldBe "OK"
    }

    @Test
    fun `gyldig ordre returnerer 200 OK`() = testApplication {
        setupApp()

        // Arrange
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        // Act
        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.gyldigOrdreRequest())
        }

        // Assert
        response.status shouldBe HttpStatusCode.OK
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe true
        body.feilmelding shouldBe null
    }

    @Test
    fun `ordre med for lav total returnerer 400 Bad Request`() = testApplication {
        setupApp()

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.ordreRequestMedTotal(50.0)) // Under minimum 100
        }

        response.status shouldBe HttpStatusCode.BadRequest
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
        body.feilmelding shouldBe "Ordre total 50.0 kr er under minimum 100.0 kr"
    }

    @Test
    fun `ordre med ukjent kunde returnerer 404 Not Found`() = testApplication {
        setupApp()

        // Ingen kunde lagt til
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.ordreRequestMedKunde(999))
        }

        response.status shouldBe HttpStatusCode.NotFound
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
        body.feilmelding shouldBe "Kunde med ID 999 ble ikke funnet"
    }

    @Test
    fun `ordre med inaktiv kunde returnerer 400 Bad Request`() = testApplication {
        setupApp()

        kundeRepository.leggTil(KundeMother.inaktivKunde(id = 123))

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.gyldigOrdreRequest())
        }

        response.status shouldBe HttpStatusCode.BadRequest
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
        body.feilmelding shouldBe "Kunde med ID 123 er inaktiv"
    }

    @Test
    fun `ordre med produkt uten lager returnerer 409 Conflict`() = testApplication {
        setupApp()

        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        // Ingen lagerbeholdning satt for P1

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.gyldigOrdreRequest())
        }

        response.status shouldBe HttpStatusCode.Conflict
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
        body.feilmelding shouldBe "Produkt P1 er utsolgt"
    }
}
