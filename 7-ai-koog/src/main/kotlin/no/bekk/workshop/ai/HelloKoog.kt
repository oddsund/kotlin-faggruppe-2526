package no.bekk.workshop.ai

// Koog API reference: docs/koog-0.8-cheatsheet.md
// Dette er en enkel fil for å sjekke at api-nøkkelen til workshopen funker

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("ANTHROPIC_API_KEY")
        ?: error("ANTHROPIC_API_KEY er ikke satt. Sett miljøvariabelen og prøv igjen.")

    val executor = simpleAnthropicExecutor(apiKey)

    val agent = AIAgent(
        promptExecutor = executor,
        systemPrompt = "Du er en hjelpsom assistent. Svar på norsk.",
        llmModel = AnthropicModels.Haiku_4_5,
    )

    val response = agent.run("Si hei til workshop-deltakerne")
    println(response)
}
