package com.freelance.timetracker.core.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import timber.log.Timber

/**
 * Generic AI access for kit apps. Use this when you need a model to write text,
 * answer a question, transform input, or produce structured JSON — anything that
 * fits a single request / response or a streaming response.
 *
 * Why "OpenRouter" in the class name? OpenRouter is one provider — a future
 * `TogetherAiRepository`, `GroqAiRepository`, or `OpenAiRepository` can live
 * alongside it without an `AiClient` interface bottleneck. Pick the provider
 * that suits your latency / cost profile; OpenRouter is shipped because it
 * proxies 100+ models behind one key.
 *
 * Methods take the `model` first — models are the load-bearing choice and the
 * one most often varied per call (e.g. cheap model for parsing, premium model
 * for generation). Find model IDs in OpenRouter's catalogue:
 * https://openrouter.ai/models
 *
 * All methods are safe to call on the main thread — IO is dispatched internally.
 */
class OpenRouterAiRepository internal constructor(
    private val client: OpenRouterAiClient,
    private val json: Json,
) {

    /**
     * Single-turn text generation. Most callers want this.
     *
     * @param model OpenRouter model ID, e.g. `"anthropic/claude-haiku-4-5"`.
     * @param prompt The user's request.
     * @param systemPrompt Optional persona / behaviour instructions.
     */
    suspend fun generateText(
        model: String,
        prompt: String,
        systemPrompt: String? = null,
    ): Result<String> {
        val messages = buildList {
            if (!systemPrompt.isNullOrBlank()) add(AiMessage.system(systemPrompt))
            add(AiMessage.user(prompt))
        }
        return generateTextWithMessages(model, messages)
    }

    /**
     * Multi-turn / advanced text generation. Use when you need to pass a full
     * conversation history (e.g. for context-aware replies) or to tune sampling.
     */
    suspend fun generateTextWithMessages(
        model: String,
        messages: List<AiMessage>,
        temperature: Double? = null,
        maxTokens: Int? = null,
    ): Result<String> = runCatching {
        val response = client.chatCompletion(
            OpenRouterChatRequest(
                model = model,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens,
            ),
        )
        response.choices.firstOrNull()?.message?.content
            ?: error("OpenRouter returned no choices for model $model")
    }.onFailure { Timber.tag(TAG).w(it, "generateText failed for model %s", model) }

    /**
     * Streaming text generation. Emits content chunks as they arrive — wire to a
     * Compose `LaunchedEffect` and append to a `mutableStateOf("")` for a typing
     * effect. The flow completes when the model finishes.
     */
    fun streamText(
        model: String,
        prompt: String,
        systemPrompt: String? = null,
    ): Flow<String> = flow {
        val messages = buildList {
            if (!systemPrompt.isNullOrBlank()) add(AiMessage.system(systemPrompt))
            add(AiMessage.user(prompt))
        }
        val body = client.chatCompletionStream(
            OpenRouterChatRequest(
                model = model,
                messages = messages,
                stream = true,
            ),
        )
        body.charStream().useLines { lines ->
            for (raw in lines) {
                val line = raw.trim()
                if (!line.startsWith(SSE_PREFIX)) continue
                val payload = line.removePrefix(SSE_PREFIX).trim()
                if (payload == SSE_DONE) break
                val chunk = runCatching {
                    json.decodeFromString(OpenRouterChatResponse.serializer(), payload)
                }.getOrNull()
                val delta = chunk?.choices?.firstOrNull()?.delta?.content
                if (!delta.isNullOrEmpty()) emit(delta)
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Structured-output generation. Returns the model's reply as a JSON string —
     * the caller decodes it with kotlinx.serialization into the type they want.
     *
     * @param jsonSchemaName Identifier for the schema (used by some providers
     *  for caching).
     * @param schema Optional JSON Schema; passed via OpenRouter's
     *  `response_format` so the model is forced to produce conforming output.
     *  Omit for free-form JSON output (model will follow the prompt's shape).
     */
    suspend fun generateJson(
        model: String,
        prompt: String,
        jsonSchemaName: String? = null,
        schema: JsonObject? = null,
        systemPrompt: String? = null,
    ): Result<String> = runCatching {
        val messages = buildList {
            if (!systemPrompt.isNullOrBlank()) add(AiMessage.system(systemPrompt))
            add(AiMessage.user(prompt))
        }
        val responseFormat = when {
            schema != null && jsonSchemaName != null -> OpenRouterResponseFormat(
                type = "json_schema",
                jsonSchema = OpenRouterJsonSchema(name = jsonSchemaName, schema = schema),
            )
            else -> OpenRouterResponseFormat(type = "json_object")
        }
        val response = client.chatCompletion(
            OpenRouterChatRequest(
                model = model,
                messages = messages,
                responseFormat = responseFormat,
            ),
        )
        response.choices.firstOrNull()?.message?.content
            ?: error("OpenRouter returned no choices for model $model")
    }.onFailure { Timber.tag(TAG).w(it, "generateJson failed for model %s", model) }

    companion object {
        private const val TAG = "OpenRouterAi"
        private const val SSE_PREFIX = "data:"
        private const val SSE_DONE = "[DONE]"
    }
}
