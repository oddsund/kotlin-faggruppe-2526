package no.bekk.workshop.integration

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.di.*
import io.ktor.server.testing.*
import no.bekk.workshop.di.configureTestDependencies
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.dto.KundeDto
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRoutingKtorDI
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KtorDITest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
    }

    private fun ApplicationTestBuilder.setupApp() {
        application {
            configureTestDependencies(kundeRepository, lagerRepository)
            configureSerialization()
            configureRoutingKtorDI()
        }
    }

    @Test
    fun `health endpoint fungerer med Ktor DI`() = testApplication {
        setupApp()

        val response = client.get("/health")

        response.status shouldBe HttpStatusCode.OK
        response.body<String>() shouldBe "OK"
    }

    @Test
    fun `valider ordre endpoint fungerer med Ktor DI`() = testApplication {
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
    fun `hent kunde endpoint fungerer med Ktor DI`() = testApplication {
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
    fun `dependencies kan resolves fra Application`() = testApplication {
        application {
            configureTestDependencies(kundeRepository, lagerRepository)

            // Verifiser at vi kan hente dependencies
            val ordreValidering: OrdreValidering by dependencies
            ordreValidering.shouldNotBeNull()

            val kundeRepo: KundeRepository by dependencies
            kundeRepo.shouldNotBeNull()

            val lagerRepo: LagerRepository by dependencies
            lagerRepo.shouldNotBeNull()
        }
    }

    @Test
    fun `Ktor DI håndterer ugyldig kunde`() = testApplication {
        setupApp()
        // Ingen kunde lagt til

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 999))
        }

        response.status shouldBe HttpStatusCode.NotFound
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
    }

    @Test
    fun `Ktor DI håndterer inaktiv kunde`() = testApplication {
        setupApp()
        kundeRepository.leggTil(Kunde.inaktiv(id = 1))

        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        response.status shouldBe HttpStatusCode.BadRequest
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
    }
}
