package reisesok.del2

import reisesok.modell.Leiebil
import reisesok.util.log

class Leiebilleverandor {

    // TODO: Gjør denne funksjonen suspendable slik at den ikke blokkerer tråden
    fun sok(destinasjon: String): List<Leiebil> {
        log("Leiebilleverandør: Starter søk mot $destinasjon")
        Thread.sleep(1000)
        log("Leiebilleverandør: Ferdig med søk mot $destinasjon")
        return listOf(
            Leiebil("Hertz", destinasjon, 500),
            Leiebil("Avis", destinasjon, 450)
        )
    }
}
