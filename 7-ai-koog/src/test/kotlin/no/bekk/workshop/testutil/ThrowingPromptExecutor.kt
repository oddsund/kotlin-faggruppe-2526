package no.bekk.workshop.testutil

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.streaming.StreamFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * PromptExecutor som kaster AssertionError på alle kall.
 * Brukes i cache-replay-tester: hvis cachen er varm skal den aldri kalles.
 * En AssertionError betyr at cachen ikke hadde svar for det aktuelle kallet (cache miss).
 */
class ThrowingPromptExecutor : PromptExecutor() {

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<Message.Response> = throw AssertionError("Cache miss — ThrowingPromptExecutor.execute ble kalt")

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): Flow<StreamFrame> = flow {
        throw AssertionError("Cache miss — ThrowingPromptExecutor.executeStreaming ble kalt")
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel,
    ): ModerationResult =
        throw AssertionError("Cache miss — ThrowingPromptExecutor.moderate ble kalt")

    override suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<List<Message.Response>> =
        throw AssertionError("Cache miss — ThrowingPromptExecutor.executeMultipleChoices ble kalt")

    override fun close() = Unit
}
