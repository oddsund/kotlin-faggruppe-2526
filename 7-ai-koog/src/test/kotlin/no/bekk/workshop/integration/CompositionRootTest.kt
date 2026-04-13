package no.bekk.workshop.integration

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.AppFactory
import no.bekk.workshop.domain.Kunde
import no.bekk.workshop.domain.Ordre
import no.bekk.workshop.domain.ValideringsResultat
import no.bekk.workshop.dto.OrdreRequest
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRouting
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.testutil.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompositionRootTest {

    private lateinit var kundeRepository: FakeKundeRepository
    private lateinit var lagerRepository: FakeLagerRepository

    @BeforeEach
    fun setup() {
        kundeRepository = FakeKundeRepository()
        lagerRepository = FakeLagerRepository()
    }

    @Test
    fun `createTestApp returnerer fungerende dependencies`() = runTest {
        // Arrange
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        lagerRepository.settBeholdning("P1", 10)

        // Act
        val deps = AppFactory.createTestApp(kundeRepository, lagerRepository)
        val resultat = deps.ordreValidering.valider(Ordre.gyldig(kundeId = 1))

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()
    }

    @Test
    fun `createTestApp med manglende kunde gir riktig feil`() = runTest {
        // Arrange - ingen kunde lagt til

        // Act
        val deps = AppFactory.createTestApp(kundeRepository, lagerRepository)
        val resultat = deps.ordreValidering.valider(Ordre.gyldig(kundeId = 999))

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeIkkeFunnet>()
    }

    @Test
    fun `full app med composition root håndterer gyldig request`() = testApplication {
        // Arrange
        kundeRepository.leggTil(Kunde.gyldig(id = 1))
        lagerRepository.settBeholdning("P1", 10)

        val deps = AppFactory.createTestApp(kundeRepository, lagerRepository)

        application {
            configureSerialization()
            configureRouting(deps.ordreValidering, deps.kundeRepository)
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        // Act
        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        // Assert
        response.status shouldBe HttpStatusCode.OK
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe true
    }

    @Test
    fun `full app med composition root håndterer ugyldig request`() = testApplication {
        // Arrange - inaktiv kunde
        kundeRepository.leggTil(Kunde.inaktiv(id = 1))
        lagerRepository.settBeholdning("P1", 10)

        val deps = AppFactory.createTestApp(kundeRepository, lagerRepository)

        application {
            configureSerialization()
            configureRouting(deps.ordreValidering, deps.kundeRepository)
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        // Act
        val response = client.post("/api/ordrer/valider") {
            contentType(ContentType.Application.Json)
            setBody(OrdreRequest.gyldig(kundeId = 1))
        }

        // Assert
        response.status shouldBe HttpStatusCode.BadRequest
        val body = response.body<ValideringsRespons>()
        body.gyldig shouldBe false
    }
}
