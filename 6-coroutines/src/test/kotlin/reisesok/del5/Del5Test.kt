package reisesok.del5

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Del 5: Feilhåndtering
 *
 * I denne delen lærer du om hvordan exceptions propagerer i coroutines,
 * og hvordan supervisorScope lar deg håndtere feil uten å kansellere alt.
 */
class Del5Test {

    /**
     * Oppgave 5.1: coroutineScope kansellerer alt ved feil
     *
     * Når én coroutine i en coroutineScope feiler, kanselleres alle andre.
     * Dette er "structured concurrency" i aksjon - alt-eller-ingenting.
     *
     * Start med en coroutineScope-basert implementasjon og observer
     * at testen kaster RuntimeException.
     */
    @Test
    fun `oppgave 5-1 - coroutineScope kansellerer alle ved feil`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            feilendeLeverandor = FeilendeLeverandor()
        )

        // Med coroutineScope vil hele søket feile når FeilendeLeverandør kaster
        val exception = assertThrows<RuntimeException> {
            reisesok.sok("Oslo")
        }

        assertTrue(
            exception.message?.contains("feilet") == true,
            "Exception bør inneholde feilmelding fra leverandør"
        )

        // Observer loggene: Fly, Hotell og Leiebil starter,
        // men de når aldri "Ferdig" fordi de blir kansellert
        // når FeilendeLeverandør kaster etter 500ms.
    }

    /**
     * Oppgave 5.2: supervisorScope gir partial results
     *
     * Med supervisorScope kan du håndtere feil fra enkeltleverandører
     * uten å kansellere de andre. Bruk runCatching rundt hver async.
     */
    @Test
    fun `oppgave 5-2 - supervisorScope gir partial results`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            feilendeLeverandor = FeilendeLeverandor()
        )

        // Med supervisorScope skal vi få resultater fra de som lykkes
        val resultat = reisesok.sok("Bergen")

        assertTrue(resultat.fly.isNotEmpty(), "Fly bør returneres selv om en annen feiler")
        assertTrue(resultat.hotell.isNotEmpty(), "Hotell bør returneres selv om en annen feiler")
        assertTrue(resultat.leiebiler.isNotEmpty(), "Leiebiler bør returneres selv om en annen feiler")

        // Verifiser at feilende leverandør ikke forsinker de andre unødvendig
        val forventetTid = 1500L // Leiebil er tregeste som lykkes
        val faktiskTid = testScheduler.currentTime

        assertTrue(
            faktiskTid <= forventetTid + 100,
            """
            Søket burde ta ~${forventetTid}ms (tregeste vellykkede leverandør), men tok ${faktiskTid}ms.

            Hint: Feilende leverandør (500ms) bør ikke blokkere de andre.
            """.trimIndent()
        )
    }

    /**
     * Bonus 5.3: Samle feilinformasjon i Sokresultat
     *
     * Utvid implementasjonen til å samle feil i Sokresultat.feil
     * med leverandørnavn og feilmelding.
     */
    @Test
    fun `bonus 5-3 - feil samles i Sokresultat`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            feilendeLeverandor = FeilendeLeverandor()
        )

        val resultat = reisesok.sok("Trondheim")

        assertTrue(resultat.feil.isNotEmpty(), "Feil bør være registrert")
        assertEquals(1, resultat.feil.size, "Skal være nøyaktig én feil")

        val feil = resultat.feil.first()
        assertTrue(
            feil.leverandor.contains("Feilende", ignoreCase = true),
            "Feil bør inneholde leverandørnavn"
        )
        assertTrue(
            feil.melding.isNotBlank(),
            "Feil bør inneholde feilmelding"
        )
    }

    /**
     * Ekstra test: Uten feilende leverandør skal alt fungere som normalt
     */
    @Test
    fun `uten feilende leverandør fungerer alt normalt`() = runTest {
        val reisesok = Reisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            feilendeLeverandor = null
        )

        val resultat = reisesok.sok("Kristiansand")

        assertTrue(resultat.fly.isNotEmpty())
        assertTrue(resultat.hotell.isNotEmpty())
        assertTrue(resultat.leiebiler.isNotEmpty())
        assertTrue(resultat.feil.isEmpty(), "Ingen feil uten feilende leverandør")
    }
}
