package no.bekk.workshop.ai.diagnose.løsningsforslag

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.executeStructured

class DiagnoseServiceLøsning(
    private val executor: PromptExecutor,
) {
    suspend fun diagnoser(beskrivelse: String): DiagnoseLøsning {
        val result = executor.executeStructured<DiagnoseLøsning>(
            prompt = prompt("diagnose-løsning") {
                system(
                    """
                    Du er en erfaren kundeserviceassistent for en nettbutikk.
                    Du mottar support-henvendelser og analyserer dem strukturert.
                    Svar alltid på norsk. Vær konkret og handlingsorientert.
                    Basér diagnosen utelukkende på informasjonen i henvendelsen.
                    """
                )
                user(beskrivelse)
            },
            model = AnthropicModels.Haiku_4_5,
        )
        return result.getOrNull()?.data ?: error("Kunne ikke generere diagnose for: $beskrivelse")
    }
}
