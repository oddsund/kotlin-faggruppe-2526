package no.bekk.workshop.ai.diagnose

import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("ANTHROPIC_API_KEY")
        ?: error("ANTHROPIC_API_KEY er ikke satt. Sett miljøvariabelen og prøv igjen.")

    val executor = simpleAnthropicExecutor(apiKey)
    val service = DiagnoseService(executor)

    val beskrivelser = listOf(
        DiagnoseService.EKSEMPEL_1,
        DiagnoseService.EKSEMPEL_2,
        DiagnoseService.EKSEMPEL_3,
    )

    for (beskrivelse in beskrivelser) {
        val diagnose = service.diagnoser(beskrivelse)
        println("Beskrivelse: $beskrivelse")
        println(diagnose)
        println("---")
    }
}
