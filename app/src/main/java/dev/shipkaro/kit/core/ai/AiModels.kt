package dev.shipkaro.kit.core.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Chat message used by every text-generation method. Role values match the
 * OpenAI / OpenRouter convention: `system`, `user`, `assistant`.
 *
 * Helper constructors keep call sites tidy: `AiMessage.user("hi")`.
 */
@Serializable
data class AiMessage(
    val role: String,
    val content: String,
) {
    companion object {
        fun system(content: String): AiMessage = AiMessage("system", content)
        fun user(content: String): AiMessage = AiMessage("user", content)
        fun assistant(content: String): AiMessage = AiMessage("assistant", content)
    }
}

// ---------------------------------------------------------------------------
// OpenRouter wire-format DTOs. Internal — kept out of the public API so a
// future second provider (Together / Groq / OpenAI direct) can have its own
// DTOs without breaking callers.
// ---------------------------------------------------------------------------

@Serializable
internal data class OpenRouterChatRequest(
    val model: String,
    val messages: List<AiMessage>,
    val temperature: Double? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    val stream: Boolean? = null,
    @SerialName("response_format") val responseFormat: OpenRouterResponseFormat? = null,
)

@Serializable
internal data class OpenRouterResponseFormat(
    val type: String,
    @SerialName("json_schema") val jsonSchema: OpenRouterJsonSchema? = null,
)

@Serializable
internal data class OpenRouterJsonSchema(
    val name: String,
    val strict: Boolean = true,
    val schema: kotlinx.serialization.json.JsonObject,
)

@Serializable
internal data class OpenRouterChatResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<OpenRouterChoice> = emptyList(),
)

@Serializable
internal data class OpenRouterChoice(
    val index: Int = 0,
    val message: AiMessage? = null,
    val delta: AiMessage? = null, // populated only on streaming chunks
    @SerialName("finish_reason") val finishReason: String? = null,
)
