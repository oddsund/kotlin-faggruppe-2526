package reisesok.valgfritt.flow

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import reisesok.del4.Aktivitetsleverandor
import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import reisesok.del4.Leiebilleverandor
import reisesok.util.log

class ProgressivtReisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val aktivitetsleverandor: Aktivitetsleverandor
) {

    fun sok(destinasjon: String): Flow<SokOppdatering> = channelFlow {
        log("ProgressivtReisesøk: Starter progressivt søk mot $destinasjon")

        // Start alle leverandører parallelt
        val flyJob = async {
            val fly = flyleverandor.sok(destinasjon)
            send(SokOppdatering.FlyOppdatering(fly.size))
            log("ProgressivtReisesøk: Fly ferdig (${fly.size} resultater)")
        }

        val hotellJob = async {
            val hotell = hotelleverandor.sok(destinasjon)
            send(SokOppdatering.HotellOppdatering(hotell.size))
            log("ProgressivtReisesøk: Hotell ferdig (${hotell.size} resultater)")
        }

        val leiebilJob = async {
            val leiebiler = leiebilleverandor.sok(destinasjon)
            send(SokOppdatering.LeiebilOppdatering(leiebiler.size))
            log("ProgressivtReisesøk: Leiebil ferdig (${leiebiler.size} resultater)")
        }

        val aktivitetJob = async {
            val aktiviteter = aktivitetsleverandor.sok(destinasjon)
            send(SokOppdatering.AktivitetOppdatering(aktiviteter.size))
            log("ProgressivtReisesøk: Aktiviteter ferdig (${aktiviteter.size} resultater)")
        }

        // Vent på alle
        flyJob.await()
        hotellJob.await()
        leiebilJob.await()
        aktivitetJob.await()

        send(SokOppdatering.Ferdig)
        log("ProgressivtReisesøk: Alle leverandører ferdige")
    }
}
