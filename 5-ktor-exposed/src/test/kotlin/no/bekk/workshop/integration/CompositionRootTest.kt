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
import no.bekk.workshop.domain.ValideringsResultat
import no.bekk.workshop.dto.ValideringsRespons
import no.bekk.workshop.plugins.configureRouting
import no.bekk.workshop.plugins.configureSerialization
import no.bekk.workshop.testutil.FakeKundeRepository
import no.bekk.workshop.testutil.FakeLagerRepository
import no.bekk.workshop.testutil.KundeMother
import no.bekk.workshop.testutil.OrdreMother
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
    fun `createTestApp fungerer med fake repositories`() = runTest {
        // Arrange
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        // Act
        val ordreValidering = AppFactory.createTestApp(kundeRepository, lagerRepository)
        val resultat = ordreValidering.valider(OrdreMother.gyldigOrdre())

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Gyldig>()
    }

    @Test
    fun `full request gjennom composition root`() = testApplication {
        // Arrange
        kundeRepository.leggTil(KundeMother.aktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        val ordreValidering = AppFactory.createTestApp(kundeRepository, lagerRepository)

        application {
            configureSerialization()
            configureRouting(ordreValidering)
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
    fun `composition root kan bytte ut repositories for testing`() = runTest {
        // Arrange - inaktiv kunde
        kundeRepository.leggTil(KundeMother.inaktivKunde(id = 123))
        lagerRepository.settBeholdning("P1", 10)

        // Act
        val ordreValidering = AppFactory.createTestApp(kundeRepository, lagerRepository)
        val resultat = ordreValidering.valider(OrdreMother.gyldigOrdre())

        // Assert
        resultat.shouldBeInstanceOf<ValideringsResultat.Ugyldig.KundeInaktiv>()
    }
}
