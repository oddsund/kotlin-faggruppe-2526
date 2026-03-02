package reisesok.valgfritt.testing

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import reisesok.util.log
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Valgfritt C: Testing med virtuell tid
 *
 * Demonstrerer hvordan runTest og TestCoroutineScheduler
 * lar deg kontrollere tid i tester.
 */
class VirtuellTidTest {

    @Test
    fun `runTest bruker virtuell tid`() = runTest {
        val startTid = currentTime
        log("Start: $startTid ms")

        delay(10_000) // 10 sekunder - men kjører umiddelbart!

        val sluttTid = currentTime
        log("Slutt: $sluttTid ms (virtuell)")

        assertEquals(10_000L, sluttTid, "Virtuell tid bør ha gått 10 sekunder")

        // Viktig: Testen tok IKKE 10 sekunder å kjøre!
        // runTest "spoler" gjennom delay() umiddelbart
    }

    @Test
    fun `advanceTimeBy lar deg kontrollere tid manuelt`() = runTest {
        var resultat = "ikke ferdig"

        // Start en coroutine som setter resultat etter 5 sekunder
        val job = async {
            delay(5000)
            resultat = "ferdig"
        }

        // Tid har ikke gått ennå
        assertEquals("ikke ferdig", resultat)
        assertEquals(0L, currentTime)

        // Flytt tid fremover med 3 sekunder
        advanceTimeBy(3000)
        assertEquals("ikke ferdig", resultat)
        assertEquals(3000L, currentTime)

        // Flytt tid fremover med 2 sekunder til (nå er vi på 5 sekunder)
        advanceTimeBy(2000)
        job.await()
        assertEquals("ferdig", resultat)
        assertEquals(5000L, currentTime)
    }

    @Test
    fun `parallellitet bevises via virtuell tid`() = runTest {
        suspend fun tregOperasjon(): String {
            delay(1000)
            return "done"
        }

        // Sekvensiell
        val sekvensiellStart = currentTime
        tregOperasjon()
        tregOperasjon()
        val sekvensiellTid = currentTime - sekvensiellStart

        assertEquals(2000L, sekvensiellTid, "Sekvensielt: 2x1000ms")

        // Reset - vi kan ikke egentlig resette, så vi noterer start
        val parallellStart = currentTime

        // Parallell
        val a = async { tregOperasjon() }
        val b = async { tregOperasjon() }
        a.await()
        b.await()

        val parallellTid = currentTime - parallellStart

        assertEquals(1000L, parallellTid, "Parallelt: maks 1000ms")

        assertTrue(
            parallellTid < sekvensiellTid,
            "Parallell eksekvering bør være raskere enn sekvensiell"
        )
    }
}
