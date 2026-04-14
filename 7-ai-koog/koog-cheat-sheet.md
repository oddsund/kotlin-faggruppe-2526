# Koog 0.8.0 Cheatsheet

Reference for writing workshop code against Koog **0.8.0** (released 10 April 2026).
Artifact: `ai.koog:koog-agents:0.8.0` (or `koog-agents-jvm` on pure JVM / Maven).

---

## 1. PromptExecutor construction

Three layers, all documented at <https://docs.koog.ai/prompts/prompt-executors/>:

- **Pre-defined `simpleXxxExecutor` helpers** — wrap a single client, return `SingleLLMPromptExecutor`.
- **`MultiLLMPromptExecutor(...)`** — wrap one or more clients, route by model provider.
- **`RoutingLLMPromptExecutor`** — distribute calls across multiple client instances (experimental).

### Pre-defined single-provider executors

```kotlin
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor

val openai    = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))
val anthropic = simpleAnthropicExecutor(System.getenv("ANTHROPIC_API_KEY"))
val google    = simpleGoogleAIExecutor(System.getenv("GOOGLE_API_KEY"))
val ollama    = simpleOllamaAIExecutor()  // defaults to http://localhost:11434
```

All four return a `PromptExecutor`. Source: <https://docs.koog.ai/prompts/prompt-executors/#pre-defined-prompt-executors>.

### Multi-provider

```kotlin
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider

val executor = MultiLLMPromptExecutor(
    LLMProvider.OpenAI    to OpenAILLMClient(System.getenv("OPENAI_API_KEY")),
    LLMProvider.Anthropic to AnthropicLLMClient(System.getenv("ANTHROPIC_API_KEY")),
)
```

### 0.7 → 0.8 notes
- 0.8.0 breaking change: `LLMProvider` singletons were restored. If you had a
  custom provider implementation that relied on `LLMProvider` being instantiable per
  use, rework it to use the singletons. Source:
  <https://github.com/JetBrains/koog/releases/tag/0.8.0> (Breaking Changes, #1800).
- 0.8.0 also decoupled `LLMClient` constructors from Ktor (#1742) — if you were
  passing a custom `HttpClientEngine`, check the constructor shape.

---

## 2. AIAgent construction

`AIAgent` itself is an `expect abstract class` (see
<https://api.koog.ai/agents/agents-core/ai.koog.agents.core.agent/-a-i-agent/index.html>).
What looks like a constructor call in docs is a companion factory that returns a
concrete `AIAgent<String, String>`. In Java, use `AIAgent.builder()`.

### Minimal (Kotlin)

```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
    llmModel       = OpenAIModels.Chat.GPT4o,
)
val result: String = agent.run("Hello!")
```

### Full parameter set (Kotlin)

```kotlin
val agent = AIAgent(
    promptExecutor = myExecutor,                  // required: PromptExecutor
    llmModel       = OpenAIModels.Chat.GPT4o,     // required: LLModel
    systemPrompt   = "You are a concise helper.", // optional, default ""
    temperature    = 0.7,                         // optional, default null
    toolRegistry   = ToolRegistry { tool(::askUser) }, // optional
    maxIterations  = 10,                          // optional, default 50
) {
    // Optional: install features in this lambda
    handleEvents {
        onToolCallStarting { ctx -> println("Tool: ${ctx.toolName}") }
    }
}
```

### Java builder

```java
AIAgent<String, String> agent = AIAgent.builder()
    .promptExecutor(simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")))
    .llmModel(OpenAIModels.Chat.GPT4o)
    .systemPrompt("You are a concise helper.")
    .temperature(0.7)
    .toolRegistry(ToolRegistry.builder().tools(new MyTools()).build())
    .maxIterations(10)
    .build();
```

Source: <https://docs.koog.ai/agents/basic-agents/>.

### 0.7 → 0.8 notes
- Constructor/builder shape unchanged.
- 0.8.0 exposes `prepareEnvironment` as an override point in agent implementations
  if you need to customize environment setup (#1790).

---

## 3. executeStructured\<T\>

Extension on `PromptExecutor`. Source:
<https://docs.koog.ai/structured-output/#layer-1-prompt-executor>.

### Signature

```kotlin
suspend inline fun <reified T> PromptExecutor.executeStructured(
    prompt: Prompt,
    model: LLModel,
    examples: List<T> = emptyList(),
    fixingParser: StructureFixingParser? = null,
): Result<StructuredResponse<T>>
```

- Returns `Result<StructuredResponse<T>>`. Parsing/validation errors surface as
  `Result.failure(...)`; call `.getOrNull()?.data` for the typed value or handle
  `.exceptionOrNull()`.
- `examples` are included in the generated prompt so the model sees a target shape.
- `fixingParser` (a `StructureFixingParser(model, retries)`) auto-retries parsing
  failures by asking an auxiliary model to repair the JSON.
- The method auto-selects native structured-output vs. manual prompting based on
  model capabilities. Use the advanced overload that takes a
  `StructuredRequestConfig<T>` for per-provider control.

### Snippet

```kotlin
val result: Result<StructuredResponse<WeatherForecast>> =
    executor.executeStructured<WeatherForecast>(
        prompt   = prompt("wx") { user("Forecast for Oslo?") },
        model    = OpenAIModels.Chat.GPT4oMini,
        examples = exampleForecasts,
        fixingParser = StructureFixingParser(
            model   = OpenAIModels.Chat.GPT4o,
            retries = 3,
        ),
    )

result.fold(
    onSuccess = { println(it.data) },
    onFailure = { println("parse failed: ${it.message}") },
)
```

### 0.7 → 0.8 notes
- 0.8.0 added **native JSON Schema** for Claude 4.5+ across Anthropic / Bedrock /
  Vertex AI (#1593) and for OpenAI models (#1822). No API change; the auto-selector
  now picks native mode on more providers.
- Note on naming from 0.6 → 0.7: `StructuredOutput` was renamed to
  `StructuredRequest`, `StructuredData` to `Structure`, `JsonStructuredData` to
  `JsonStructure`. If you're copying from pre-0.7 tutorials, rename accordingly.

---

## 4. ToolSet + @Tool + @LLMDescription

JVM-only. Source: <https://docs.koog.ai/annotation-based-tools/>.

```kotlin
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Tools for inspecting a local file system")
class FileTools : ToolSet {

    @Tool
    @LLMDescription("Read the contents of a text file and return it as a string")
    fun readFile(
        @LLMDescription("Absolute path to the file")
        path: String,
    ): String = java.io.File(path).readText()

    @Tool
    @LLMDescription("List names of files in a directory")
    fun listFiles(
        @LLMDescription("Absolute path to the directory")
        dir: String,
    ): String = java.io.File(dir).list()?.joinToString("\n") ?: ""
}
```

Register on an agent:

```kotlin
val agent = AIAgent(
    promptExecutor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
    llmModel       = OpenAIModels.Chat.GPT4o,
    toolRegistry   = ToolRegistry { tools(FileTools()) },  // plural: registers all @Tool methods
)
```

### Notes
- `@Tool` targets `FUNCTION`; `@LLMDescription` targets class, function,
  parameter, property, and type.
- For Kotlin top-level functions, `ToolRegistry { tool(::myFun) }` also works.
- Annotation-based tools are JVM-only. For multiplatform, use class-based tools
  (<https://docs.koog.ai/class-based-tools/>).

---

## 5. CachedPromptExecutor and FilePromptCache

Wraps any `PromptExecutor`. Source: <https://docs.koog.ai/prompts/llm-response-caching/>.

```kotlin
import ai.koog.prompt.cache.files.FilePromptCache
import ai.koog.prompt.executor.cached.CachedPromptExecutor
import kotlin.io.path.Path

val real = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))

val cached = CachedPromptExecutor(
    cache  = FilePromptCache(Path("build/koog-cache")),
    nested = real,
)

val response = cached.execute(prompt, OpenAIModels.Chat.GPT4o)
```

### Behavior
- Cache key is derived from prompt + model. A hit returns the cached
  `List<Message.Response>` without calling the nested executor.
- **Default on miss**: call the nested executor, store the result, return it.
  There is no documented "throw on miss" mode — enforce that externally (e.g.
  wrap with your own executor that checks the cache first and throws).
- `FilePromptCache(Path)` stores one file per cache entry under the given
  directory. The directory is created if it does not exist.
- `executeStreaming()` on a cached executor returns the cached response as a
  single chunk. `moderate()` bypasses the cache. `executeMultipleChoices()` is
  not cached.

---

## 6. Koog Ktor plugin

Artifact: `ai.koog:koog-ktor`. Source: <https://docs.koog.ai/ktor-plugin/>.

```kotlin
import ai.koog.ktor.Koog
import ai.koog.ktor.aiAgent
import ai.koog.ktor.llm
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.llm.LLMProvider
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.module() {
    install(Koog) {
        llm {
            openAI(apiKey = System.getenv("OPENAI_API_KEY") ?: "")
            anthropic(apiKey = System.getenv("ANTHROPIC_API_KEY") ?: "")
            fallback {
                provider = LLMProvider.OpenAI
                model    = OpenAIModels.Chat.GPT4_1
            }
        }
        agentConfig {
            prompt("agent") { system("You are a server-side agent") }
            maxAgentIterations = 10
            registerTools { /* tool(::myTool) */ }
        }
    }

    routing {
        post("/chat") {
            val input = call.receiveText()
            val output = aiAgent(
                strategy = reActStrategy(),
                model    = OpenAIModels.Chat.GPT4_1,
                input    = input,
            )
            call.respond(output)
        }
        post("/llm") {
            val msgs = llm().execute(
                prompt("direct") { user(call.receiveText()) },
                OpenAIModels.Chat.GPT4_1,
            )
            call.respond(msgs.joinToString("") { it.content })
        }
    }
}
```

### Config in `application.yaml`

```yaml
koog:
  openai:
    apikey: ${OPENAI_API_KEY}
  anthropic:
    apikey: ${ANTHROPIC_API_KEY}
  llm:
    fallback:
      provider: openai
      model: openai.chat.gpt4_1
```

### 0.7 → 0.8 notes
- 0.8.0 renamed a `registerTools` parameter in `koog-ktor` internals to avoid
  `Builder.build()` shadowing (#1705, #1721). Config surface is unchanged for
  users, but if you extended the builder in code, check the parameter name.

---

## 7. OpenTelemetry tracing

Agent feature. Source: <https://docs.koog.ai/features/open-telemetry/>. There is
**no separate "enable tracing on an executor"** API — tracing is installed on
the agent, which then instruments the executor calls it makes.

### Enable

```kotlin
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter

val agent = AIAgent(
    promptExecutor = executor,
    llmModel       = OpenAIModels.Chat.GPT4o,
) {
    install(OpenTelemetry) {
        setServiceInfo("workshop-agent", "1.0.0")
        addSpanExporter(LoggingSpanExporter.create())
        addSpanExporter(
            OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build()
        )
        setVerbose(true)  // include message content in spans (masked otherwise)
    }
}
```

### What's in the traces
Spans (hierarchical): `CreateAgentSpan` → `InvokeAgentSpan` → `StrategySpan` →
`NodeExecuteSpan` → `InferenceSpan` (LLM call) / `ExecuteToolSpan` / `SubgraphExecuteSpan` / `McpClientSpan`.

Attributes follow the OpenTelemetry **gen_ai semantic conventions**. This
includes `gen_ai.conversation.id`, model name, and on `InferenceSpan`, token
counts and latency (derived from span duration). Koog-specific attributes use
the `koog.` prefix: `koog.strategy.name`, `koog.node.id`, `koog.node.input`,
`koog.node.output`, `koog.subgraph.id` etc.

Events attached to spans: `SystemMessageEvent`, `UserMessageEvent`,
`AssistantMessageEvent`, `ToolMessageEvent`, `ChoiceEvent`, `ModerationResponseEvent`.

### Integrating with an existing OpenTelemetry SDK

Two options:
1. **Let Koog build its own SDK** — call `addSpanExporter(...)`,
   `addSpanProcessor(...)`, `addResourceAttributes(...)`, `setSampler(...)`.
   Koog builds an `OpenTelemetrySdk` from these.
2. **Inject your pre-built SDK** — call `setSdk(mySdk)`. When you do this,
   `addSpanExporter` / `addSpanProcessor` / `addResourceAttributes` / `setSampler`
   are **ignored**. Koog just obtains a `Tracer` from your SDK using
   `serviceName`/`serviceVersion` as the instrumentation scope.

### 0.7 → 0.8 notes
- 0.8.0 added a **DataDog LLM Observability exporter** with response metadata
  forwarded to `InferenceSpan` (#1591). See
  <https://docs.koog.ai/features/open-telemetry/opentelemetry-datadog-exporter/>.
- 0.7 earlier aligned span attributes with gen_ai semantic conventions; older
  tutorials may show differently-named attributes.

---

## 8. Structured output with @Serializable data classes

Source: <https://docs.koog.ai/structured-output/>.

```kotlin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ai.koog.prompt.structure.annotations.LLMDescription

@Serializable
@SerialName("WeatherForecast")
@LLMDescription("A weather forecast for one city")
data class WeatherForecast(
    @property:LLMDescription("City name")
    val city: String,
    @property:LLMDescription("Temperature in Celsius")
    val temperatureC: Int,
    @property:LLMDescription("Main conditions")
    val conditions: Conditions,
    @property:LLMDescription("Alerts active for this forecast")
    val alerts: List<Alert> = emptyList(),
)

@Serializable
@SerialName("Conditions")
enum class Conditions { Sunny, Cloudy, Rainy, Snowy }

@Serializable
sealed class Alert {
    abstract val message: String

    @Serializable @SerialName("Storm")
    data class Storm(override val message: String, val windKph: Double) : Alert()

    @Serializable @SerialName("Flood")
    data class Flood(override val message: String, val rainMm: Double) : Alert()
}
```

### Required annotations
- `@Serializable` — from kotlinx.serialization. Required on the class and every
  nested / enum / sealed subtype.
- `@SerialName("...")` — controls the type name used in the JSON schema and the
  polymorphic discriminator for sealed classes.
- `@LLMDescription` on the class — describes the overall type to the model.
- `@property:LLMDescription` on fields — constructor parameters in a data class
  need the `property:` site target, otherwise the annotation attaches to the
  parameter and the schema generator won't pick it up on the property.

### Supported nesting
- **Nested data classes**: fully supported.
- **Collections**: `List<T>` and `Map<String, T>` are supported.
- **Enums**: serialized as a string constrained to the enum values.
- **Sealed classes**: polymorphism is supported with `StandardJsonSchemaGenerator`
  (the default for `JsonStructure` since 0.5). The discriminator key is `kind`
  (renamed from `#type` in 0.5). `BasicJsonSchemaGenerator` does **not** support
  polymorphism — pick it only for flat structures on weak models.

### Using it
See section 3. Typical call:

```kotlin
val r = executor.executeStructured<WeatherForecast>(
    prompt = prompt("wx") { user("Forecast for Oslo?") },
    model  = OpenAIModels.Chat.GPT4oMini,
)
```

### 0.7 → 0.8 notes
- Native JSON Schema is used on more providers in 0.8.0 (Claude 4.5+ native on
  Anthropic / Bedrock / Vertex AI per #1593; added for OpenAI per #1822).
- `AdditionalPropertiesFlatteningSerializer` now supports customized field
  names (#1626) — relevant only if you use that serializer explicitly.

---

## Sources

- Release notes 0.8.0: <https://github.com/JetBrains/koog/releases/tag/0.8.0>
- Docs: <https://docs.koog.ai/>
- API reference: <https://api.koog.ai/>
