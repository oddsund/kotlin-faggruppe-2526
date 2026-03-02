package reisesok.del4

import kotlinx.coroutines.delay
import reisesok.modell.Leiebil
import reisesok.util.log

class Leiebilleverandor {

    suspend fun sok(destinasjon: String): List<Leiebil> {
        log("Leiebilleverandør: Starter søk mot $destinasjon")
        delay(1500) // Tregere enn fly og hotell
        log("Leiebilleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Leiebil("Hertz", destinasjon, 500),
            Leiebil("Avis", destinasjon, 450)
        )
    }
}
