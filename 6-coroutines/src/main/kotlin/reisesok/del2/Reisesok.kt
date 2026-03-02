package reisesok.del2

import reisesok.modell.Sokresultat

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor
) {

    // TODO: Implementer denne funksjonen som en suspend-funksjon
    // som kaller alle tre leverandørene sekvensielt og returnerer et samlet resultat.
    fun sok(destinasjon: String): Sokresultat {
        TODO("Implementer sekvensielt søk")
    }
}
