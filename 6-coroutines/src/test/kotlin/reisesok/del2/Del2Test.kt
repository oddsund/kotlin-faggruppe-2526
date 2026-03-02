package reisesok.del2

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import reisesok.util.log
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Del 2: Builders (blokkerende -> suspend)
 *
 * I denne delen skal du gjøre leverandørene til suspend-funksjoner
 * som bruker delay() i stedet for Thread.sleep().
 */
class Del2Test {

    /**
     * Oppgave 2.1: Verifiser at providers ikke blokkerer tråden
     *
     * Når leverandørene bruker delay() i stedet for Thread.sleep(),
     * kan flere coroutines kjøre "samtidig" på samme tråd.
     * To samtidige søk bør ta ~1000ms virtuell tid, ikke ~2000ms.
     *
     * Merk: Testen sjekker at virtuell tid er ~1000ms (ikke 0 eller 2000ms).
     * - 0ms betyr at du bruker Thread.sleep() (blokkerer, men ingen virtuell tid)
     * - 2000ms betyr at du bruker delay(), men kaller sekvensielt
     * - 1000ms betyr at du bruker delay() og kjører parallelt
     */
    @Test
    fun `oppgave 2-1 - providers skal ikke blokkere tråden`() = runTest {
        val flyleverandor = Flyleverandor()

        val sok1 = async { flyleverandor.sok("Oslo") }
        val sok2 = async { flyleverandor.sok("Bergen") }

        sok1.await()
        sok2.await()

        val forventetTidMs = 1000L
        val faktiskTidMs = testScheduler.currentTime

        // Sjekk at vi bruker delay() (virtuell tid > 0)
        assertTrue(
            faktiskTidMs > 0,
            """
            Virtuell tid er 0ms - dette betyr at du bruker Thread.sleep() i stedet for delay().

            Thread.sleep() blokkerer tråden (faktisk ventetid), men runTest ser ikke dette.
            delay() suspenderer coroutinen og registreres som virtuell tid.

            Hint: Gjør funksjonen til en suspend-funksjon og bytt Thread.sleep(1000) til delay(1000).
            """.trimIndent()
        )

        // Sjekk at begge kjører parallelt (maks ~1000ms, ikke ~2000ms)
        assertTrue(
            faktiskTidMs <= forventetTidMs + 100,
            """
            To samtidige søk burde ta ~${forventetTidMs}ms virtuell tid, men tok ${faktiskTidMs}ms.

            Hint: Har du lagt til suspend keyword på funksjonen?
            """.trimIndent()
        )
    }

    /**
     * Oppgave 2.2: Verifiser at sekvensiell søketjeneste tar ~3000ms
     *
     * Reisesok.sok() skal kalle alle tre leverandørene sekvensielt.
     * Med tre providers à 1000ms bør total tid bli ~3000ms.
     * Husk å oppdater leverandører med suspend!
     */
    @Test
    fun `oppgave 2-2 - sekvensiell søketjeneste tar summen av leverandørtider`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor()
        )

        val resultat = reisesok.sok("Tromsø")

        val forventetTidMs = 3000L
        val faktiskTidMs = testScheduler.currentTime

        assertTrue(
            faktiskTidMs in (forventetTidMs - 100)..(forventetTidMs + 100),
            """
            Sekvensielt søk med tre providers à 1000ms burde ta ~${forventetTidMs}ms, men tok ${faktiskTidMs}ms.

            Hint: Kaller du leverandørene sekvensielt (én etter én)?
            I del 4 skal vi gjøre dette parallelt, men først må vi forstå sekvensiell flyt.
            """.trimIndent()
        )

        assertTrue(resultat.fly.isNotEmpty(), "Resultat skal inneholde fly")
        assertTrue(resultat.hotell.isNotEmpty(), "Resultat skal inneholde hotell")
        assertTrue(resultat.leiebiler.isNotEmpty(), "Resultat skal inneholde leiebiler")
    }

    /**
     * Bonus 2.3: launch vs async (Job vs Deferred)
     *
     * OPPGAVE: Kjør denne testen og observer loggene.
     *
     * 1. Først kjører vi med launch - vi får IKKE resultatet tilbake
     * 2. Deretter kjører vi med async - vi FÅR resultatet tilbake
     *
     * Prøv å endre koden:
     * - Hva skjer hvis du prøver å bruke `launchResultat` som en liste?
     * - Kan du hente verdien fra launch på noen måte?
     */
    @Test
    fun `bonus 2-3 - launch vs async demonstrasjon`() = runTest {
        val flyleverandor = Flyleverandor()

        // ===== LAUNCH =====
        log("--- Starter launch ---")

        val job = launch {
            val fly = flyleverandor.sok("Stockholm")
            log("launch: Fikk ${fly.size} fly, men hvordan får vi dem ut?")
            // Hvordan skal vi hente ut fly variabelen?
        }

        job.join() // Vent til launch er ferdig

        log("launch: launchResultat har ${0} fly")
        log("launch: Men dette er tungvint - vi måtte bruke en variabel utenfor")

        // ===== ASYNC =====
        log("--- Starter async ---")

        val deferred = async {
            val fly = flyleverandor.sok("København")
            log("async: Fikk ${fly.size} fly, returnerer dem direkte")
            // Hva gjør vi her for at deferred skal inneholde fly?
        }

        val asyncResultat = deferred.await() // Hent verdien direkte!

        log("async: asyncResultat har ${0} fly")
        log("async: Mye enklere - await() gir oss verdien")

        // ===== OPPSUMMERING =====
        // Oppdater det 0-tallet med riktig variabel
        assertEquals(2, 0, "launch: Feil verdi - hvordan skal vi få ut verdien?")
        assertEquals(2, 0, "async: Feil verdi - hvordan skal vi få ut verdien?")

        // KONKLUSJON:
        // - Bruk `async` når du trenger resultatet (returnerer Deferred<T>)
        // - Bruk `launch` når du bare vil starte noe uten å vente på resultat (returnerer Job)
    }
}
