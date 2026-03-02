package reisesok.valgfritt.flow

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import reisesok.del4.Aktivitetsleverandor
import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import reisesok.del4.Leiebilleverandor
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Valgfritt B: Flow
 *
 * Lær å bruke Flow for progressive oppdateringer.
 */
class FlowTest {

    @Test
    fun `flow emitter oppdateringer etter hvert som leverandører returnerer`() = runTest {
        val progressivtReisesok = ProgressivtReisesok(
            flyleverandor = Flyleverandor(),
            hotelleverandor = Hotelleverandor(),
            leiebilleverandor = Leiebilleverandor(),
            aktivitetsleverandor = Aktivitetsleverandor()
        )

        val oppdateringer = progressivtReisesok.sok("Tromsø").toList()

        // Første oppdatering bør være Aktivitet (800ms)
        // Deretter Fly og Hotell (1000ms)
        // Til slutt Leiebil (1500ms) og Ferdig

        assertTrue(oppdateringer.isNotEmpty(), "Skal motta oppdateringer")

        val sisteOppdatering = oppdateringer.last()
        assertEquals(SokOppdatering.Ferdig, sisteOppdatering, "Siste oppdatering skal være Ferdig")

        // Verifiser at vi fikk alle typer oppdateringer
        assertTrue(
            oppdateringer.any { it is SokOppdatering.FlyOppdatering },
            "Skal inneholde FlyOppdatering"
        )
        assertTrue(
            oppdateringer.any { it is SokOppdatering.HotellOppdatering },
            "Skal inneholde HotellOppdatering"
        )
        assertTrue(
            oppdateringer.any { it is SokOppdatering.LeiebilOppdatering },
            "Skal inneholde LeiebilOppdatering"
        )
        assertTrue(
            oppdateringer.any { it is SokOppdatering.AktivitetOppdatering },
            "Skal inneholde AktivitetOppdatering"
        )
    }
}
