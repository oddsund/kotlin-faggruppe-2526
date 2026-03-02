package reisesok.del3

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import reisesok.util.log
import kotlin.test.assertTrue

/**
 * Del 3: Structured concurrency og context
 *
 * Denne delen har ingen src/main-kode. Her utforsker vi hvordan
 * coroutines er strukturert og hvordan context arves.
 */
class Del3Test {

    /**
     * Oppgave 3.1: Forutsi rekkefølgen
     *
     * OPPGAVE: Før du kjører testen, skriv ned hvilken rekkefølge
     * du tror loggmeldingene vil komme i. Kjør så testen og sammenlign.
     *
     * Tips: coroutineScope venter på alle barn før den returnerer.
     *
     * Scroll ned til bunnen av filen for forklaring og "fasit"!
     */
    @Test
    fun `oppgave 3-1 - forutsi rekkefølgen av loggmeldinger`() = runTest {
        log("1: Før coroutineScope")

        coroutineScope {
            log("2: Inne i coroutineScope, før launch")

            launch {
                log("3: Inne i første launch, før delay")
                delay(100)
                log("4: Inne i første launch, etter delay")
            }

            launch {
                log("5: Inne i andre launch, før delay")
                delay(50)
                log("6: Inne i andre launch, etter delay")
            }

            log("7: Inne i coroutineScope, etter begge launch")
        }

        log("8: Etter coroutineScope")
    }

    /**
     * Oppgave 3.2: CoroutineName-arv
     *
     * Observer hvordan CoroutineName arves fra parent til children,
     * og hvordan et barn kan overstyre med sitt eget navn.
     *
     * Scroll ned til bunnen av filen for forklaring og "fasit"!
     */
    @Test
    fun `oppgave 3-2 - coroutine context arves fra parent`() = runTest {
        withContext(CoroutineName("ParentCoroutine")) {
            val parentNavn = coroutineContext[CoroutineName]?.name
            log("Parent har navn: $parentNavn")

            launch {
                val arvetNavn = coroutineContext[CoroutineName]?.name
                log("Barn 1 (arver) har navn: $arvetNavn")
            }

            launch(CoroutineName("EgetNavnCoroutine")) {
                val egetNavn = coroutineContext[CoroutineName]?.name
                log("Barn 2 (eget navn) har navn: $egetNavn")
            }
        }
    }

    /**
     * Bonus 3.3: Hva skjer uten coroutineScope?
     *
     * OPPGAVE: Denne testen feiler! Fiks den.
     *
     * GlobalScope lager coroutines som IKKE er knyttet til parent.
     * Det betyr at parent ikke venter på dem automatisk.
     *
     * Hint: Hvordan kan du vente på at en Job skal fullføre?
     */
    @Test
    fun `bonus 3-3 - uten coroutineScope venter ikke parent`() = runTest {
        log("1: Før GlobalScope.launch")

        var coroutineFullført = false

        // GlobalScope er vanligvis feil valg!
        // Vi bruker det her kun for å demonstrere problemet.
        val job = GlobalScope.launch {
            log("2: Inne i GlobalScope.launch, før delay")
            delay(100)
            log("3: Inne i GlobalScope.launch, etter delay")
            coroutineFullført = true
        }

        log("4: Etter GlobalScope.launch (venter vi?)")

        // TODO: Testen feiler fordi vi ikke venter på job.
        // Legg til kode her som venter på at job skal fullføre.
        // Hint: Job har en metode som heter join()

        log("5: Etter venting")

        // Denne assertion feiler hvis du ikke venter på job!
        assertTrue(
            coroutineFullført,
            """
            Coroutinen fullførte ikke før testen avsluttet!

            Uten å vente på GlobalScope-coroutinen, fortsetter testen
            og avslutter før coroutinen er ferdig.

            Hint: Bruk job.join() for å vente på at coroutinen fullfører.
            """.trimIndent()
        )
    }

    /**
     * Bonus 3.4: Dispatchers og tråder
     *
     * Denne testen viser hvordan forskjellige dispatchers påvirker
     * hvilken tråd coroutinen kjører på.
     *
     * Scroll ned til bunnen av filen for forklaring og "fasit"!
     */
    @Test
    fun `bonus 3-4 - dispatchers påvirker hvilken tråd som brukes`() = runTest {
        log("Standard (runTest): ${Thread.currentThread().name}")

        withContext(Dispatchers.Default) {
            log("Dispatchers.Default: ${Thread.currentThread().name}")
        }

        withContext(Dispatchers.IO) {
            log("Dispatchers.IO: ${Thread.currentThread().name}")
        }

        // Dispatchers.Main krever UI-framework (Android, JavaFX, etc.)
        // og vil kaste exception her
    }
}

// ============================================================================
// FORKLARING/"FASIT" - Ikke scroll ned før du har prøvd selv!
// ============================================================================
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
// ============================================================================
// Oppgave 3.1 - Forventet rekkefølge:
// ============================================================================
//
// 1 -> 2 -> 3 -> 5 -> 7 -> 6 -> 4 -> 8
//
// Forklaring:
// - 1, 2: Kjører før vi starter coroutines
// - 3, 5: launch starter umiddelbart, men suspenderer ved delay
// - 7: Resten av coroutineScope-blokken kjører
// - 6: Andre launch (50ms) fullfører før første (100ms)
// - 4: Første launch fullfører
// - 8: coroutineScope returnerer når alle barn er ferdige
//
// ============================================================================
// Oppgave 3.2 - Forventet output:
// ============================================================================
//
// Parent har navn: ParentCoroutine
// Barn 1 (arver) har navn: ParentCoroutine
// Barn 2 (eget navn) har navn: EgetNavnCoroutine
//
// ============================================================================
// Bonus 3.3 - Løsning:
// ============================================================================
//
// Legg til: job.join()
//
// Med GlobalScope venter vi IKKE automatisk.
// Output uten job.join(): 1 -> 2 -> 4 -> 5 (3 kommer aldri!)
// Output med job.join(): 1 -> 2 -> 4 -> 3 -> 5
//
// ============================================================================
// Bonus 3.4 - Forventet output (tråd-navn varierer):
// ============================================================================
//
// Standard (runTest): Test worker @coroutine#1
// Dispatchers.Default: DefaultDispatcher-worker-1 @coroutine#1
// Dispatchers.IO: DefaultDispatcher-worker-2 @coroutine#1
