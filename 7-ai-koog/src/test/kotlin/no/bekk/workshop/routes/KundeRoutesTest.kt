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
import no.bekk.workshop.dto.KundeDto
import no.bekk.workshop.dto.OpprettKundeRequest
import no.bekk.workshop.plugins.configureRouting
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KundeRoutesTest {

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
    fun `GET kunder-id returnerer kunde når den finnes`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.gyldig(id = 1, navn = "Test Kunde"))

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.get("/api/kunder/1")

        response.status shouldBe HttpStatusCode.OK
        val kunde = response.body<KundeDto>()
        kunde.id shouldBe 1
        kunde.navn shouldBe "Test Kunde"
    }

    @Test
    fun `GET kunder-id returnerer 404 når kunde ikke finnes`() = testApplication {
        setupApp()

        val response = client.get("/api/kunder/999")

        response.status shouldBe HttpStatusCode.NotFound
    }

    @Test
    fun `GET kunder-id returnerer 400 for ugyldig id`() = testApplication {
        setupApp()

        val response = client.get("/api/kunder/abc")

        response.status shouldBe HttpStatusCode.BadRequest
    }

    @Test
    fun `GET kunder returnerer liste av kunder`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.gyldig(id = 1, navn = "Kunde 1"))
        kundeRepository.leggTil(Kunde.gyldig(id = 2, navn = "Kunde 2"))

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.get("/api/kunder")

        response.status shouldBe HttpStatusCode.OK
        val kunder = response.body<List<KundeDto>>()
        kunder.size shouldBe 2
    }

    @Test
    fun `POST kunder oppretter kunde og returnerer 201`() = testApplication {
        setupApp()

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/kunder") {
            contentType(ContentType.Application.Json)
            setBody(OpprettKundeRequest(navn = "Ny Kunde"))
        }

        response.status shouldBe HttpStatusCode.Created
        response.headers["Location"] shouldBe "/api/kunder/1"
    }
}
