package reisesok.del2

import reisesok.modell.Sokresultat
import reisesok.util.log

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor
) {

    suspend fun sok(destinasjon: String): Sokresultat {
        log("Reisesøk: Starter sekvensielt søk mot $destinasjon")

        // Sekvensielle kall - én etter én
        val fly = flyleverandor.sok(destinasjon)
        val hotell = hotelleverandor.sok(destinasjon)
        val leiebiler = leiebilleverandor.sok(destinasjon)

        log("Reisesøk: Ferdig med sekvensielt søk")

        return Sokresultat(
            fly = fly,
            hotell = hotell,
            leiebiler = leiebiler
        )
    }
}
