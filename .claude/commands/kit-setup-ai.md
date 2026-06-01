---
description: Wire OpenRouter so the app can call any AI model with one key
---

You are running **`/kit-setup-ai`** for ShipKit. Goal: enable
`OpenRouterAiRepository` so the developer can call any of 100+ AI models behind
a single OpenRouter API key.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/ai

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Why OpenRouter

> **One key, every model.** OpenRouter is a proxy: pay once at OpenRouter, get
> access to Anthropic's Claude, OpenAI's GPT, Google's Gemini, Meta's Llama,
> Mistral, DeepSeek and dozens more behind one API. The wire format is
> OpenAI-compatible, so swapping to a direct provider later is a base-URL change.
>
> **This integration is isolated.** OpenRouter has its own Retrofit instance
> in the kit (separate base URL, separate auth header). Your own backend
> (`KitConfig.API_BASE_URL`) never collides with it — adding a second AI
> provider later (Together, Groq, raw OpenAI) gets its own client class too.

## Step 1 — Decide whether to wire AI

Use AskUserQuestion: "Will this app call AI models?" If no, set
`OPENROUTER_ENABLED = false` in `KitConfig.kt` (it's the default) and stop.

## Step 2 — Get an OpenRouter key

Show:

> 1. Sign in at https://openrouter.ai
> 2. Open https://openrouter.ai/keys → **Create Key** → name it after this app.
> 3. Copy the key (starts with `sk-or-v1-…`). Add **5 USD of credits** at
>    https://openrouter.ai/credits — free-tier models work without credits but
>    are heavily rate-limited.

## Step 3 — Write the key into `local.properties`

`local.properties` is git-ignored. Append:

    openrouter.api.key=sk-or-v1-YOUR_OPENROUTER_KEY

If `local.properties` doesn't exist yet, copy it from
`local.properties.template` first.

## Step 4 — Pick a default model

Use AskUserQuestion. Offer four buckets — each option's description shows
typical cost per 1M tokens and what it's good for:

- **Fast & free** — `meta-llama/llama-3.2-3b-instruct:free`. Zero cost, rate-limited.
- **Cheap workhorse** — `google/gemini-2.5-flash`. Cheap, fast, good for parsing / summarising.
- **Premium reasoning** — `anthropic/claude-sonnet-4-6`. Best general-purpose model in 2026; not free.
- **Pick another** — let the dev paste a model ID from https://openrouter.ai/models.

Write the choice into `KitConfig.OPENROUTER_DEFAULT_MODEL`. Flip
`KitConfig.OPENROUTER_ENABLED = true`.

## Step 5 — Show the dev how to call it

Print verbatim:

> Inject the repository where you need AI:
>
> ```kotlin
> import dev.shipkaro.kit.core.ai.OpenRouterAiRepository
> import dev.shipkaro.kit.core.config.KitConfig
> import org.koin.compose.koinInject
>
> @Composable
> fun MyScreen() {
>     val ai = koinInject<OpenRouterAiRepository>()
>     // ...
>     LaunchedEffect(Unit) {
>         val result = ai.generateText(
>             model = KitConfig.OPENROUTER_DEFAULT_MODEL,
>             prompt = "Write a haiku about Android development.",
>         )
>         result.onSuccess { /* show it */ }.onFailure { /* surface error */ }
>     }
> }
> ```
>
> Other methods on the repository:
>  - `generateTextWithMessages(model, messages, temperature, maxTokens)` — full
>    conversation history.
>  - `streamText(model, prompt)` — `Flow<String>` of chunks for a typing effect.
>  - `generateJson(model, prompt, jsonSchemaName, schema)` — forces structured
>    JSON output.
>
> Inside ViewModels: `org.koin.android.ext.android.inject` or constructor
> injection in `featureModule` (see `AppModules.kt`).

## Step 6 — Verify

**Skip this step if you are running as part of another orchestrator.**
Otherwise run `./gradlew :app:compileDebugKotlin`.

Report:
- The default model picked.
- A reminder that without credits, only `:free` models work, and even those are
  rate-limited.
