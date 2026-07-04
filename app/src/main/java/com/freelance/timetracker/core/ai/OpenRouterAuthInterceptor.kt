package com.freelance.timetracker.core.ai

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the `Authorization: Bearer <key>` header to every OpenRouter request,
 * plus OpenRouter's optional `HTTP-Referer` / `X-Title` headers used by their
 * dashboard to attribute traffic. `apiKey` is empty when no key has been
 * configured — the interceptor skips the header in that case so the call still
 * goes through (and OpenRouter returns a 401, which surfaces a clean error
 * to the developer).
 */
internal class OpenRouterAuthInterceptor(
    private val apiKey: String,
    private val appName: String? = null,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (apiKey.isNotBlank()) {
            builder.addHeader("Authorization", "Bearer $apiKey")
        }
        if (!appName.isNullOrBlank()) {
            builder.addHeader("X-Title", appName)
        }
        return chain.proceed(builder.build())
    }
}
