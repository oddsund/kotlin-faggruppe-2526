package reisesok.del5

import reisesok.modell.Sokresultat

class Reisesok(
    private val flyleverandor: Flyleverandor,
    private val hotelleverandor: Hotelleverandor,
    private val leiebilleverandor: Leiebilleverandor,
    private val feilendeLeverandor: FeilendeLeverandor? = null
) {

    // TODO (Oppgave 5.1): Start med coroutineScope-basert løsning fra del 4.
    // Observer hva som skjer når FeilendeLeverandør kaster exception.
    //
    // TODO (Oppgave 5.2): Bytt til supervisorScope og bruk runCatching
    // for å håndtere feil fra enkeltleverandører uten å kansellere de andre.
    // Feil bør samles i Sokresultat.feil.
    suspend fun sok(destinasjon: String): Sokresultat {
        TODO("Implementer feilhåndtering med supervisorScope")
    }
}
