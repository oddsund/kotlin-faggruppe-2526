package reisesok.del4

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import reisesok.util.log
import kotlin.test.assertTrue

/**
 * Del 4: Parallell eksekvering
 *
 * I denne delen skal du implementere parallelt søk med coroutineScope og async.
 */
class Del4Test {

    /**
     * Oppgave 4.1: Verifiser at parallelt søk tar ~1500ms (maks av providers)
     *
     * Når alle leverandører kjører parallelt, bør total tid være lik
     * den tregeste leverandøren (Leiebilleverandør med 1500ms),
     * ikke summen av alle (3500ms).
     */
    @Test
    fun `oppgave 4-1 - parallelt søk tar maks av leverandørtider`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor()
        )

        val resultat = reisesok.sok("Tromsø")

        val forventetMaksTid = 1500L
        val forventetSumTid = 3500L
        val faktiskTidMs = testScheduler.currentTime

        assertTrue(
            faktiskTidMs <= forventetMaksTid + 100,
            """
            Parallelt søk burde ta ~${forventetMaksTid}ms (maks av providers), men tok ${faktiskTidMs}ms.

            ${if (faktiskTidMs >= forventetSumTid - 100) """
            Det ser ut som leverandørene kjører sekvensielt (${forventetSumTid}ms).

            Hint: Kaller du async { } for alle providers FØR du kaller .await()?

            FEIL (sekvensiell):
                val fly = async { flyleverandor.sok(dest) }.await()  // Venter her!
                val hotell = ...

            RIKTIG (parallell):
                val flyDeferred = async { flyleverandor.sok(dest) }
                ...
                val fly = flyDeferred.await()  // Venter først her!
            """.trimIndent() else """
            Hint: Bruker du coroutineScope med async for hver leverandør?
            """.trimIndent()}
            """.trimIndent()
        )

        assertTrue(resultat.fly.isNotEmpty(), "Resultat skal inneholde fly")
        assertTrue(resultat.hotell.isNotEmpty(), "Resultat skal inneholde hotell")
        assertTrue(resultat.leiebiler.isNotEmpty(), "Resultat skal inneholde leiebiler")
    }

    /**
     * Oppgave 4.2: Verifiser at fire providers parallelt fortsatt tar ~1500ms
     *
     * Med Aktivitetsleverandør (800ms) i tillegg bør tiden fortsatt være ~1500ms
     * fordi alle kjører parallelt.
     */
    @Test
    fun `oppgave 4-2 - fire providers parallelt tar fortsatt maks tid`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            aktivitetsleverandor = Aktivitetsleverandor()
        )

        val resultat = reisesok.sok("Stavanger")

        val forventetMaksTid = 1500L
        val faktiskTidMs = testScheduler.currentTime

        assertTrue(
            faktiskTidMs <= forventetMaksTid + 100,
            """
            Fire parallelle providers burde fortsatt ta ~${forventetMaksTid}ms, men tok ${faktiskTidMs}ms.

            Hint: Sjekk at du starter async for Aktivitetsleverandør også
            (hvis den er satt) før du kaller await.
            """.trimIndent()
        )

        assertTrue(resultat.fly.isNotEmpty(), "Resultat skal inneholde fly")
        assertTrue(resultat.hotell.isNotEmpty(), "Resultat skal inneholde hotell")
        assertTrue(resultat.leiebiler.isNotEmpty(), "Resultat skal inneholde leiebiler")
        assertTrue(resultat.aktiviteter.isNotEmpty(), "Resultat skal inneholde aktiviteter")
    }

    /**
     * Bonus 4.3: Demonstrasjon av tråd-bytte med withContext
     *
     * Denne testen viser hvordan withContext(Dispatchers.IO) bytter tråd
     * for I/O-operasjoner.
     */
    @Test
    fun `bonus 4-3 - withContext bytter dispatcher`() = runTest {
        log("Før withContext: ${Thread.currentThread().name}")

        withContext(Dispatchers.IO) {
            log("Inne i Dispatchers.IO: ${Thread.currentThread().name}")
            // Her ville du typisk gjort blokkerende I/O
        }

        log("Etter withContext: ${Thread.currentThread().name}")

        // withContext suspenderer til blokken er ferdig, så rekkefølgen er garantert:
        // 1. Før withContext
        // 2. Inne i Dispatchers.IO (på en IO-tråd)
        // 3. Etter withContext (tilbake på original tråd)
    }
}
