package no.bekk.workshop.integration

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import no.bekk.workshop.domain.OrdreValidering
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRoutingKoin
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository
import no.bekk.workshop.testutil.FakeKundeRepository
import no.bekk.workshop.testutil.FakeLagerRepository
import no.bekk.workshop.testutil.KundeMother
import no.bekk.workshop.testutil.OrdreMother
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.ext.inject

class KoinModuleTest {

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    private fun Application.installKoin(testModule: org.koin.core.module.Module) {
        install(Koin) {
            modules(testModule)
        }
    }

    @Test
    fun `testModule gir fungerende dependencies`() = testApplication {
        // Arrange
        val kundeRepository = FakeKundeRepository()
        val lagerRepository = FakeLagerRepository()

        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        val testModule = module {
            single<KundeRepository> { kundeRepository }
            single<LagerRepository> { lagerRepository }
            single { OrdreValidering(get(), get()) }
        }

        application {
            installKoin(testModule)
            configureSerialization()
            configureRoutingKoin()
        }

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
    }

    @Test
    fun `testModule kan overstyre med fakes for feilsituasjoner`() = testApplication {
        // Arrange - ingen kunde
        val kundeRepository = FakeKundeRepository()
        val lagerRepository = FakeLagerRepository()

        val testModule = module {
            single<KundeRepository> { kundeRepository }
            single<LagerRepository> { lagerRepository }
            single { OrdreValidering(get(), get()) }
        }

        application {
            installKoin(testModule)
            configureSerialization()
            configureRoutingKoin()
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        // Act
        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreMother.ordreRequestMedKunde(999))
        }

        // Assert
        response.status shouldBe HttpStatusCode.NotFound
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
        body.feilmelding shouldBe "Kunde med ID 999 ble ikke funnet"
    }

    @Test
    fun `koin kan injisere OrdreValidering`() = testApplication {
        val kundeRepository = FakeKundeRepository()
        val lagerRepository = FakeLagerRepository()

        val testModule = module {
            single<KundeRepository> { kundeRepository }
            single<LagerRepository> { lagerRepository }
            single { OrdreValidering(get(), get()) }
        }

        application {
            installKoin(testModule)

            // Verifiser at vi kan hente OrdreValidering fra Koin
            val ordreValidering by inject<OrdreValidering>()
            ordreValidering.shouldNotBeNull()
        }
    }
}
