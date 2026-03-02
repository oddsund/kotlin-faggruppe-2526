package reisesok.del4

import kotlinx.coroutines.delay
import reisesok.modell.Aktivitet
import reisesok.util.log

class Aktivitetsleverandor {

    suspend fun sok(destinasjon: String): List<Aktivitet> {
        log("Aktivitetsleverandør: Starter søk mot $destinasjon")
        delay(800) // Raskere enn de andre
        log("Aktivitetsleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Aktivitet("Fjelltur", destinasjon, 200),
            Aktivitet("Byvandring", destinasjon, 150)
        )
    }
}
