package reisesok.del5

import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import reisesok.modell.Sokfeil
import reisesok.modell.Sokresultat
import reisesok.util.log

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val feilendeLeverandor: FeilendeLeverandor? = null
) {

    // Oppgave 5.1: coroutineScope-versjon (kaster ved feil)
    // Utkommenter denne og bruk supervisorScope-versjonen for 5.2

    /*
    suspend fun sokMedCoroutineScope(destinasjon: String): Sokresultat = coroutineScope {
        val flyDeferred = async { flyleverandor.sok(destinasjon) }
        val hotellDeferred = async { hotelleverandor.sok(destinasjon) }
        val leiebilDeferred = async { leiebilleverandor.sok(destinasjon) }
        val feilendeDeferred = feilendeLeverandor?.let {
            async { it.sok(destinasjon) }
        }

        // Når feilendeLeverandør kaster, kanselleres alle andre
        Sokresultat(
            fly = flyDeferred.await(),
            hotell = hotellDeferred.await(),
            leiebiler = leiebilDeferred.await()
        )
    }
    */

    // Oppgave 5.2 og 5.3: supervisorScope med runCatching
    suspend fun sok(destinasjon: String): Sokresultat = supervisorScope {
        log("Reisesøk: Starter søk mot $destinasjon med feilhåndtering")
        val feil = mutableListOf<Sokfeil>()

        // Start alle parallelt - supervisorScope lar oss håndtere feil individuelt
        val flyDeferred = async {
            runCatching { flyleverandor.sok(destinasjon) }
        }
        val hotellDeferred = async {
            runCatching { hotelleverandor.sok(destinasjon) }
        }
        val leiebilDeferred = async {
            runCatching { leiebilleverandor.sok(destinasjon) }
        }
        val feilendeDeferred = feilendeLeverandor?.let {
            async {
                runCatching { it.sok(destinasjon) }
            }
        }

        // Hent resultater og håndter feil
        val fly = flyDeferred.await().getOrElse {
            feil.add(Sokfeil("Flyleverandør", it.message ?: "Ukjent feil"))
            emptyList()
        }

        val hotell = hotellDeferred.await().getOrElse {
            feil.add(Sokfeil("Hotelleverandør", it.message ?: "Ukjent feil"))
            emptyList()
        }

        val leiebiler = leiebilDeferred.await().getOrElse {
            feil.add(Sokfeil("Leiebilleverandør", it.message ?: "Ukjent feil"))
            emptyList()
        }

        // Håndter feilende leverandør hvis den finnes
        feilendeDeferred?.await()?.onFailure {
            feil.add(Sokfeil("FeilendeLeverandør", it.message ?: "Ukjent feil"))
        }

        log("Reisesøk: Ferdig med ${feil.size} feil")

        Sokresultat(
            fly = fly,
            hotell = hotell,
            leiebiler = leiebiler,
            feil = feil
        )
    }
}
