package reisesok.valgfritt.flow

import kotlinx.coroutines.flow.Flow
import reisesok.del4.Aktivitetsleverandor
import reisesok.del4.Flyleverandor
import reisesok.del4.Hotelleverandor
import reisesok.del4.Leiebilleverandor

sealed class SokOppdatering {
    data class FlyOppdatering(val antall: Int) : SokOppdatering()
    data class HotellOppdatering(val antall: Int) : SokOppdatering()
    data class LeiebilOppdatering(val antall: Int) : SokOppdatering()
    data class AktivitetOppdatering(val antall: Int) : SokOppdatering()
    data object Ferdig : SokOppdatering()
}

class ProgressivtReisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val aktivitetsleverandor: Aktivitetsleverandor
) {

    // TODO: Implementer en Flow som emitter oppdateringer etter hvert
    // som leverandører returnerer. Bruk flow { } builder og channelFlow
    // for å starte parallelle coroutines som sender oppdateringer.
    fun sok(destinasjon: String): Flow<SokOppdatering> {
        TODO("Implementer progressivt søk med Flow")
    }
}
