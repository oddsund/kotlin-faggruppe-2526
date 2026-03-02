package reisesok.valgfritt.mdc

import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import reisesok.util.log
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Valgfritt D: MDC (Mapped Diagnostic Context)
 *
 * Demonstrerer hvordan MDC (logging context) kan tapes
 * i coroutines, og hvordan MDCContext løser problemet.
 */
class MdcTest {

    @Test
    fun `MDC tapes uten MDCContext`() = runTest {
        log("=== Test: MDC tapes uten MDCContext ===")

        MDC.put("requestId", "abc-123")
        log("Satte MDC requestId = 'abc-123'")
        log("Før withContext: MDC.get('requestId') = '${MDC.get("requestId")}'")

        val mdcVerdi = withContext(Dispatchers.Default) {
            // MDC er tråd-lokal, og vi er nå på en annen tråd
            val verdi = MDC.get("requestId")
            log("Inne i Dispatchers.Default: MDC.get('requestId') = '$verdi' (TAPT!)")
            verdi
        }

        log("Etter withContext: MDC.get('requestId') = '${MDC.get("requestId")}'")

        // MDC-verdien er tapt fordi vi byttet tråd
        assertNull(
            mdcVerdi,
            "MDC tapes når vi bytter tråd uten MDCContext"
        )

        MDC.clear()
    }

    @Test
    fun `MDCContext bevarer MDC på tvers av tråder`() = runTest {
        log("=== Test: MDCContext bevarer MDC ===")

        MDC.put("requestId", "xyz-789")
        log("Satte MDC requestId = 'xyz-789'")
        log("Før withContext: MDC.get('requestId') = '${MDC.get("requestId")}'")

        val mdcVerdi = withContext(Dispatchers.Default + MDCContext()) {
            // MDCContext kopierer MDC til den nye tråden
            val verdi = MDC.get("requestId")
            log("Inne i Dispatchers.Default + MDCContext: MDC.get('requestId') = '$verdi' (BEVART!)")
            verdi
        }

        log("Etter withContext: MDC.get('requestId') = '${MDC.get("requestId")}'")

        assertEquals(
            "xyz-789",
            mdcVerdi,
            "MDCContext bevarer MDC-verdier på tvers av tråder"
        )

        MDC.clear()
    }

    @Test
    fun `parallelle coroutines med MDC`() = runTest {
        log("=== Test: Parallelle coroutines med MDC ===")

        MDC.put("correlationId", "main-flow")
        log("Satte MDC correlationId = 'main-flow'")

        val resultater = withContext(MDCContext()) {
            log("Inne i MDCContext, starter parallelle coroutines...")

            val a = async(Dispatchers.Default) {
                val verdi = MDC.get("correlationId")
                log("Coroutine A (Default): MDC correlationId = '$verdi'")
                "Coroutine A ser: $verdi"
            }
            val b = async(Dispatchers.IO) {
                val verdi = MDC.get("correlationId")
                log("Coroutine B (IO): MDC correlationId = '$verdi'")
                "Coroutine B ser: $verdi"
            }

            listOf(a.await(), b.await())
        }

        log("Begge coroutines fullført med MDC bevart!")

        assertEquals(
            "Coroutine A ser: main-flow",
            resultater[0]
        )
        assertEquals(
            "Coroutine B ser: main-flow",
            resultater[1]
        )

        MDC.clear()
    }

    @Test
    fun `MDCContext arves ikke automatisk til children`() = runTest {
        log("=== Test: MDCContext arves ikke automatisk ===")

        MDC.put("userId", "user-42")
        log("Satte MDC userId = 'user-42'")

        withContext(MDCContext()) {
            log("Parent (med MDCContext): MDC userId = '${MDC.get("userId")}'")

            // Men launch uten MDCContext mister det når vi bytter dispatcher
            val job = launch(Dispatchers.Default) {
                val verdi = MDC.get("userId")
                log("Child launch (uten MDCContext): MDC userId = '$verdi' (kan være null!)")
            }
            job.join()

            // Med MDCContext på child også
            val job2 = launch(Dispatchers.Default + MDCContext()) {
                val verdi = MDC.get("userId")
                log("Child launch (med MDCContext): MDC userId = '$verdi' (bevart!)")
            }
            job2.join()
        }

        MDC.clear()
    }
}
