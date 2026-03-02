package reisesok.del2

import kotlinx.coroutines.delay
import reisesok.modell.Hotell
import reisesok.util.log

class Hotelleverandor {

    suspend fun sok(destinasjon: String): List<Hotell> {
        log("Hotelleverandør: Starter søk mot $destinasjon")
        delay(1000) // Byttet fra Thread.sleep(1000)
        log("Hotelleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Hotell("Grand Hotel", destinasjon, 1500),
            Hotell("Budget Inn", destinasjon, 800)
        )
    }
}
