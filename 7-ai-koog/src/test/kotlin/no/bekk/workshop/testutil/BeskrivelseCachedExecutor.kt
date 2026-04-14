package no.bekk.workshop.testutil

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.ResponseMetaInfo
import ai.koog.prompt.streaming.StreamFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
private data class CachedResponses(val messages: List<String>)

/**
 * Filbasert cache-executor som nøkler på siste brukermelding i prompten (beskrivelsen).
 * Erstatter Koog's CachedPromptExecutor + FilePromptCache, som produserer ustabile nøkler
 * på tvers av kjøringer og dermed aldri treffer cachen.
 *
 * Replay-modus (record=false): returnerer cachet svar. Cache-miss = AssertionError.
 * Recording-modus (record=true): kaller live-executoren og lagrer svaret til disk.
 */
class BeskrivelseCachedExecutor(
    private val cacheDir: Path,
    private val record: Boolean,
    private val live: PromptExecutor,
) : PromptExecutor() {

    private val json = Json { prettyPrint = true }

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<Message.Response> {
        val beskrivelse = prompt.messages
            .filterIsInstance<Message.User>()
            .lastOrNull()
            ?.content
            ?: error("Ingen brukermelding i prompten")

        val key = sha256(beskrivelse).take(16)
        val cacheFile = cacheDir.resolve("$key.json")

        if (cacheFile.exists()) {
            val cached = json.decodeFromString<CachedResponses>(cacheFile.readText())
            return cached.messages.map { text ->
                Message.Assistant(text, ResponseMetaInfo.Empty)
            }
        }

        if (!record) {
            throw AssertionError("Cache miss for key $key (beskrivelse: $beskrivelse)")
        }

        val responses = live.execute(prompt, model, tools)
        val texts = responses.map { it.content }
        cacheDir.createDirectories()
        cacheFile.writeText(json.encodeToString(CachedResponses.serializer(), CachedResponses(texts)))
        return responses
    }

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): Flow<StreamFrame> = flow {
        throw AssertionError("BeskrivelseCachedExecutor støtter ikke streaming")
    }

    override suspend fun moderate(
        prompt: Prompt,
        model: LLModel,
    ): ModerationResult =
        throw AssertionError("BeskrivelseCachedExecutor støtter ikke moderate")

    override suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>,
    ): List<List<Message.Response>> =
        throw AssertionError("BeskrivelseCachedExecutor støtter ikke executeMultipleChoices")

    override fun close() = live.close()

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
