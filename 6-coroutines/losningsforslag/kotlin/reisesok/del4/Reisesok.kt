package reisesok.del4

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import reisesok.modell.Sokresultat
import reisesok.util.log

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val aktivitetsleverandor: Aktivitetsleverandor? = null
) {

    suspend fun sok(destinasjon: String): Sokresultat = coroutineScope {
        log("Reisesøk: Starter parallelt søk mot $destinasjon")

        // Start alle leverandører parallelt med async
        val flyDeferred = async { flyleverandor.sok(destinasjon) }
        val hotellDeferred = async { hotelleverandor.sok(destinasjon) }
        val leiebilDeferred = async { leiebilleverandor.sok(destinasjon) }
        val aktivitetDeferred = aktivitetsleverandor?.let {
            async { it.sok(destinasjon) }
        }

        // Vent på alle resultater
        val fly = flyDeferred.await()
        val hotell = hotellDeferred.await()
        val leiebiler = leiebilDeferred.await()
        val aktiviteter = aktivitetDeferred?.await() ?: emptyList()

        log("Reisesøk: Ferdig med parallelt søk")

        Sokresultat(
            fly = fly,
            hotell = hotell,
            leiebiler = leiebiler,
            aktiviteter = aktiviteter
        )
    }
}
