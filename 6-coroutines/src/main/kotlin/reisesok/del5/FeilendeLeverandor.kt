package reisesok.del5

import kotlinx.coroutines.delay
import reisesok.util.log

class FeilendeLeverandor {

    suspend fun sok(destinasjon: String): List<Nothing> {
        log("FeilendeLeverandør: Starter søk mot $destinasjon")
        delay(500)
        log("FeilendeLeverandør: 💥 KASTER EXCEPTION")
        throw RuntimeException("Leverandør feilet for $destinasjon")
    }
}
