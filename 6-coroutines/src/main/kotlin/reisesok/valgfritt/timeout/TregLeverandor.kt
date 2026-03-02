package reisesok.valgfritt.timeout

import kotlinx.coroutines.delay
import reisesok.util.log

class TregLeverandor {

    suspend fun sok(destinasjon: String): List<String> {
        log("TregLeverandør: Starter søk mot $destinasjon (tar 5 sekunder)")
        delay(5000)
        log("TregLeverandør: Ferdig med søk mot $destinasjon")
        return listOf("Treg data 1", "Treg data 2")
    }
}
