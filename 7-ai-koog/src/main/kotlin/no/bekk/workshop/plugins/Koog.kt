package no.bekk.workshop.plugins

import ai.koog.ktor.Koog
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.llm.LLMProvider
import io.ktor.server.application.*

fun Application.configureKoog() {
    install(Koog) {
        llm {
            anthropic(apiKey = System.getenv("ANTHROPIC_API_KEY") ?: "")
            fallback {
                provider = LLMProvider.Anthropic
                model = AnthropicModels.Haiku_4_5
            }
        }
        agentConfig {
            prompt("workshop-agent") { system("Du er en assistent for kundeservice.") }
            maxAgentIterations = 10
        }
    }
}
