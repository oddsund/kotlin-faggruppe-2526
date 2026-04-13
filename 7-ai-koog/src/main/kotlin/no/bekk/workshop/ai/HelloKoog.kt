package no.bekk.workshop.ai

// Koog API reference: docs/koog-0.8-cheatsheet.md
// This file is a setup checkpoint for participants at the start of the workshop.
// Running it should print a response from the LLM, proving their environment works.

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
        llmModel = AnthropicModels.Haiku_4_5
    )

    val response = agent.run("Si hei")
    println(response)
}
