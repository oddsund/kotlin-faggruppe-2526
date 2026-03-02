package reisesok.del2

import kotlinx.coroutines.delay
import reisesok.modell.Fly
import reisesok.util.log

class Flyleverandor {

    suspend fun sok(destinasjon: String): List<Fly> {
        log("Flyleverandør: Starter søk mot $destinasjon")
        delay(1000) // Byttet fra Thread.sleep(1000)
        log("Flyleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Fly("SK123", "SAS", destinasjon, 2500),
            Fly("DY456", "Norwegian", destinasjon, 1800)
        )
    }
}
