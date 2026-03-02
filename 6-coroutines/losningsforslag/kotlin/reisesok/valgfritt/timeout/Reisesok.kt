package reisesok.valgfritt.timeout

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import reisesok.modell.Sokresultat
import reisesok.util.log

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val tregLeverandor: TregLeverandor
) {

    suspend fun sok(destinasjon: String): Sokresultat = coroutineScope {
        log("Reisesøk: Starter søk med timeout mot $destinasjon")

        val flyDeferred = async { flyleverandor.sok(destinasjon) }
        val hotellDeferred = async { hotelleverandor.sok(destinasjon) }

        // Wrap treg leverandør i withTimeoutOrNull
        val tregDeferred = async {
            withTimeoutOrNull(2000) {
                tregLeverandor.sok(destinasjon)
            }
        }

        val fly = flyDeferred.await()
        val hotell = hotellDeferred.await()
        val tregData = tregDeferred.await() // Kan være null ved timeout

        if (tregData == null) {
            log("Reisesøk: TregLeverandør timet ut")
        }

        Sokresultat(
            fly = fly,
            hotell = hotell
        )
    }
}
