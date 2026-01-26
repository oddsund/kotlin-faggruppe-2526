package no.bekk.workshop.integration

import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.dto.KundeDto
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRoutingKoin
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

class RoutingKoinTest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    private fun ApplicationTestBuilder.setupApp() {
        val testModule = module {
            single<KundeRepository> { kundeRepository }
            single<LagerRepository> { lagerRepository }
            single { OrdreValidering(get(), get()) }
        }

        application {
            install(Koin) {
                modules(testModule)
            }
            configureSerialization()
            configureRoutingKoin()
        }
    }

    @Test
    fun `health endpoint fungerer med Koin`() = testApplication {
        setupApp()

        val response = client.get("/health")

        response.status shouldBe HttpStatusCode.OK
        response.body<String>() shouldBe "OK"
    }

    @Test
    fun `valider ordre endpoint fungerer med Koin`() = testApplication {
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
    fun `hent kunde endpoint fungerer med Koin`() = testApplication {
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
    fun `hent kunde returnerer 404 når ikke funnet med Koin`() = testApplication {
        setupApp()

        val response = client.get("/api/kunder/999")

        response.status shouldBe HttpStatusCode.NotFound
    }
}
