/*
 * Replay-modus (default): bruker cache-fiksturer fra src/test/resources/koog-cache.
 * Cache-miss = testfeil.
 *
 * Recording-modus: kjør med RECORD_CACHE=true og ANTHROPIC_API_KEY satt.
 * Testen kaller live API og lagrer responsen i cache. Commit cache-filene.
 *
 * For å re-generere en fikstur: slett den aktuelle cache-filen og kjør i recording-modus.
 *
 * NB: Bruker en egendefinert BeskrivelseCachedExecutor, ikke Koog's innebygde
 * CachedPromptExecutor + FilePromptCache. Årsak: Koog 0.8.0 produserer ustabile
 * cache-nøkler på tvers av kjøringer, slik at cachen aldri treffer.
 * BeskrivelseCachedExecutor nøkler stabilt på siste brukermelding (beskrivelsen).
 */
package no.bekk.workshop.ai.diagnose

import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import io.kotest.matchers.string.shouldNotBeBlank
import kotlinx.coroutines.test.runTest
import no.bekk.workshop.ai.diagnose.løsningsforslag.DiagnoseServiceLøsning
import no.bekk.workshop.testutil.BeskrivelseCachedExecutor
import no.bekk.workshop.testutil.ThrowingPromptExecutor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class DiagnoseServiceCachedTest {

    private lateinit var service: DiagnoseServiceLøsning

    @BeforeEach
    fun setup() {
        val record = System.getenv("RECORD_CACHE") == "true"
        val live = if (record) {
            val apiKey = System.getenv("ANTHROPIC_API_KEY")
                ?: error("RECORD_CACHE=true krever ANTHROPIC_API_KEY")
            simpleAnthropicExecutor(apiKey)
        } else {
            ThrowingPromptExecutor()
        }
        val executor = BeskrivelseCachedExecutor(
            cacheDir = Path("src/test/resources/koog-cache"),
            record = record,
            live = live,
        )
        service = DiagnoseServiceLøsning(executor)
    }

    @Test
    fun `diagnoser lagerproblem fra cache`() = runTest {
        val diagnose = service.diagnoser(
            "Kunde 42 ringer og sier de ikke får fullført ordre 12345, noe om for få varer på lager"
        )

        diagnose.sammendrag.shouldNotBeBlank()
        diagnose.sannsynligÅrsak.shouldNotBeBlank()
        diagnose.foreslåttHandling.shouldNotBeBlank()
    }

    @Test
    fun `diagnoser inaktiv kunde fra cache`() = runTest {
        val diagnose = service.diagnoser(
            "Bestilling av P001 fungerer ikke, kunden får 'kunde inaktiv' opp"
        )

        diagnose.sammendrag.shouldNotBeBlank()
        diagnose.sannsynligÅrsak.shouldNotBeBlank()
        diagnose.foreslåttHandling.shouldNotBeBlank()
    }
}
