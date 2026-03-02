package reisesok.del2

import reisesok.modell.Hotell
import reisesok.util.log

class Hotelleverandor {

    // TODO: Gjør denne funksjonen suspendable slik at den ikke blokkerer tråden
    fun sok(destinasjon: String): List<Hotell> {
        log("Hotelleverandør: Starter søk mot $destinasjon")
        Thread.sleep(1000)
        log("Hotelleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Hotell("Grand Hotel", destinasjon, 1500),
            Hotell("Budget Inn", destinasjon, 800)
        )
    }
}
