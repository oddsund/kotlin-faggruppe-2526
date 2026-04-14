package no.bekk.workshop.ai.diagnose

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.model.executeStructured
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import no.bekk.workshop.ai.diagnose.løsningsforslag.DiagnoseServiceLøsning
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("LLM-as-judge — krever live API. Aktiver lokalt for kvalitetsmåling.")
class DiagnoseEvaluationTest {

    @Serializable
    private data class Vurdering(
        val score: Int,
        val begrunnelse: String,
    )

    @Test
    fun `diagnose for lagerproblem scorer minst 3 på spesifisitet og handlingsorientering`() = runTest {
        val apiKey = System.getenv("ANTHROPIC_API_KEY")
            ?: error("ANTHROPIC_API_KEY er ikke satt")
        val executor = simpleAnthropicExecutor(apiKey)
        val service = DiagnoseServiceLøsning(executor)

        val diagnose = service.diagnoser(
            "Kunde 42 ringer og sier de ikke får fullført ordre 12345, noe om for få varer på lager"
        )

        val diagnoseTekst = """
            Sammendrag: ${diagnose.sammendrag}
            Årsak: ${diagnose.sannsynligÅrsak}
            Handling: ${diagnose.foreslåttHandling}
        """.trimIndent()

        val vurderingResult = executor.executeStructured<Vurdering>(
            prompt = prompt("evaluering") {
                system("Du er en kvalitetsvurderer for kundeservice-diagnoser.")
                user(
                    "Score følgende diagnose 1–5 på spesifisitet og handlingsorientering. " +
                        "1 = veldig generell/ubrukelig, 5 = svært konkret og handlingsorientert.\n\n$diagnoseTekst"
                )
            },
            model = AnthropicModels.Haiku_4_5,
        )

        val vurdering = vurderingResult.getOrNull()?.data
            ?: error("Kunne ikke evaluere diagnose")

        vurdering.score shouldBeGreaterThanOrEqualTo 3
    }
}
