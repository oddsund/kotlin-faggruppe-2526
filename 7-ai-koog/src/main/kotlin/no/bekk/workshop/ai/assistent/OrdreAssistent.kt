package no.bekk.workshop.ai.assistent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.model.PromptExecutor
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository

class OrdreAssistent(
    private val executor: PromptExecutor,
    private val kundeRepo: KundeRepository,
    private val lagerRepo: LagerRepository,
) {
    private val agent = AIAgent(
        promptExecutor = executor,
        llmModel = AnthropicModels.Haiku_4_5,
        systemPrompt = TODO("Skriv en god system-prompt for ordre-assistenten"),
        toolRegistry = ToolRegistry { tools(OrdreTools(kundeRepo, lagerRepo)) },
    )

    suspend fun spør(spørsmål: String): String = agent.run(spørsmål)
}
