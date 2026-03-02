package reisesok.valgfritt.timeout

import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import reisesok.modell.Sokresultat

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val tregLeverandor: TregLeverandor
) {

    // TODO: Implementer søk med timeout på 2000ms for TregLeverandør.
    // Bruk withTimeoutOrNull slik at treg leverandør ikke blokkerer de andre.
    // Total tid bør bli ~2000ms (timeout), og treg leverandør gir tom liste.
    suspend fun sok(destinasjon: String): Sokresultat {
        TODO("Implementer søk med withTimeoutOrNull")
    }
}
