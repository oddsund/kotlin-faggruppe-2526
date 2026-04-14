package no.bekk.workshop.ai.diagnose

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.executeStructured

class DiagnoseService(
    private val executor: PromptExecutor,
) {
    suspend fun diagnoser(beskrivelse: String): Diagnose {
        val systemPrompt: String = TODO("Skriv en god system-prompt for support-assistenten")
        val result = executor.executeStructured<Diagnose>(
            prompt = prompt("diagnose") {
                system(systemPrompt)
                user(beskrivelse)
            },
            model = AnthropicModels.Haiku_4_5,
        )
        return result.getOrNull()?.data ?: error("Kunne ikke generere diagnose for: $beskrivelse")
    }

    companion object {
        const val EKSEMPEL_1 =
            "Kunde 42 ringer og sier de ikke får fullført ordre 12345, noe om for få varer på lager"
        const val EKSEMPEL_2 =
            "Bestilling av P001 fungerer ikke, kunden får 'kunde inaktiv' opp"
        const val EKSEMPEL_3 =
            "Total på ordren ble 50 kr, men systemet sier den må være over 100"
    }
}
