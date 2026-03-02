package reisesok.del2

import reisesok.modell.Fly
import reisesok.util.log

class Flyleverandor {

    // TODO: Gjør denne funksjonen suspendable slik at den ikke blokkerer tråden
    fun sok(destinasjon: String): List<Fly> {
        log("Flyleverandør: Starter søk mot $destinasjon")
        Thread.sleep(1000)
        log("Flyleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Fly("SK123", "SAS", destinasjon, 2500),
            Fly("DY456", "Norwegian", destinasjon, 1800)
        )
    }
}
