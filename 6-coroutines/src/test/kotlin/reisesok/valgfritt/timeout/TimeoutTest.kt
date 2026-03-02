package reisesok.valgfritt.timeout

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import kotlin.test.assertTrue

/**
 * Valgfritt A: Timeout
 *
 * Lær å bruke withTimeoutOrNull for å sette timeout på trege operasjoner.
 */
class TimeoutTest {

    @Test
    fun `treg leverandør bør ikke blokkere andre`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            tregLeverandor = TregLeverandor()
        )

        val resultat = reisesok.sok("Oslo")

        val forventetMaksTid = 2000L // Timeout
        val faktiskTid = testScheduler.currentTime

        assertTrue(
            faktiskTid <= forventetMaksTid + 100,
            """
            Med 2000ms timeout burde søket ta ~${forventetMaksTid}ms, men tok ${faktiskTid}ms.

            Hint: Bruk withTimeoutOrNull(2000) rundt TregLeverandør.
            """.trimIndent()
        )

        assertTrue(resultat.fly.isNotEmpty(), "Fly bør returneres")
        assertTrue(resultat.hotell.isNotEmpty(), "Hotell bør returneres")
    }

    @Test
    fun `timeout gir null i stedet for exception`() = runTest {
        val tregLeverandor = TregLeverandor()

        val resultat = kotlinx.coroutines.withTimeoutOrNull(2000) {
            tregLeverandor.sok("Bergen")
        }

        assertTrue(
            resultat == null,
            "withTimeoutOrNull bør returnere null ved timeout"
        )

        val tid = testScheduler.currentTime
        assertTrue(
            tid == 2000L,
            "Timeout bør trigge etter nøyaktig 2000ms virtuell tid"
        )
    }
}
