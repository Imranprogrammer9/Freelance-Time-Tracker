package dev.shipkaro.kit.core.ai

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * Retrofit interface for OpenRouter's chat-completions endpoint.
 *
 * OpenRouter exposes an OpenAI-compatible REST surface at
 * `https://openrouter.ai/api/v1/`. Auth is a bearer token (the OpenRouter key)
 * — added by [OpenRouterAuthInterceptor], not per-call.
 *
 * This client is OpenRouter-specific by design. A future second provider
 * (Together / Groq / OpenAI direct) gets its own `*AiClient` so each can
 * model the provider's quirks without leaky abstractions. The shared layer is
 * the `*AiRepository` public surface in [OpenRouterAiRepository].
 */
internal interface OpenRouterAiClient {

    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: OpenRouterChatRequest): OpenRouterChatResponse

    @Streaming
    @POST("chat/completions")
    suspend fun chatCompletionStream(@Body request: OpenRouterChatRequest): ResponseBody
}
