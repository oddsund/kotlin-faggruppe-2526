package reisesok.del4

import reisesok.modell.Sokresultat

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val aktivitetsleverandor: Aktivitetsleverandor? = null
) {

    // TODO: Implementer denne funksjonen slik at alle leverandører
    // kalles parallelt med async/await inne i en coroutineScope.
    // Total tid bør være lik den tregeste leverandøren (~1500ms), ikke summen.
    suspend fun sok(destinasjon: String): Sokresultat {
        TODO("Implementer parallelt søk med coroutineScope og async")
    }
}
