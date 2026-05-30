---
name: onboarding-questionnaire
description: Build a personalised multi-screen Calm / Headspace-style onboarding flow that asks the user goal-based questions and tailors the app from their answers. Replaces the kit's default 3-page intro pager with a 10-14 screen interactive questionnaire (welcome → value-prop → 3-5 question screens → permission priming → social proof → personalised plan → ready). Use when the developer asks to build interactive onboarding, personalised onboarding, questionnaire onboarding, multi-screen onboarding, Calm-style or Headspace-style intro, an onboarding quiz, or wants the app to feel personalised from first launch.
---

You are running the **onboarding-questionnaire** skill — build a personalised
multi-screen onboarding flow for a ShipKit app.

## What this builds vs the kit default

The kit ships a simple `OnboardingScreen` (3 swipeable pages with placeholder
copy customised by `/kit-start-setup`). This skill REPLACES that with a
multi-screen interactive flow modelled on Headspace / Calm:

> Welcome → Pain → Solution → Quiz intro → Question screens × 3–5 → Social
> proof → Personalised plan → Permission priming → Auth nudge (if enabled) →
> Ready

10–14 screens depending on how many questions land. Answers persist in
DataStore so other screens can read them.

## Pre-flight

Before starting:
- App must compile — run `/kit-compile-app`. Stop if it does not.
- Read `app/build.gradle.kts` `namespace` to find the base package — do NOT
  hardcode `dev.shipkaro.kit` (the dev may have run `/kit-change-app-id`).
- Read `KitConfig` so you know the app's `AUTH_ENABLED` + `PAYWALL_ENABLED`
  state — the flow's auth nudge / paywall transition follows them.

## Step 1 — Analyse the app

Read the project to understand what app you're building onboarding for:
- `feature/` directory — what screens exist?
- `app/src/main/res/values/strings.xml` — `app_name` + onboarding placeholder
  strings (`onboarding_page1_title` ... `onboarding_page3_desc`) hold the
  one-sentence pitch if `/kit-start-setup` Step 2 ran.

Summarise in 2–3 lines what app you think this is. Ask the developer to
correct or confirm before proposing the flow.

If the brand tone is not obvious from the existing copy, ask
(AskUserQuestion): **Minimal / Friendly / Bold / Playful**.

## Step 2 — Propose the flow

Based on the app, propose a 10–14 screen flow. Use this template, swap in
app-specific content:

> 1. **Welcome** — brand mark + tagline + "Get started" CTA.
> 2. **Pain point** — name the problem the app solves.
> 3. **Solution** — show how the app solves it.
> 4. **Quiz intro** — "A few questions so we can personalise <app>" + Continue.
> 5. **Question 1** — primary goal (e.g. "What's your main goal?").
> 6. **Question 2** — frequency / commitment (e.g. "How often do you want to
>    practise?").
> 7. **Question 3** — pace / starting point (e.g. "Where are you starting from?").
> 8. **Question 4** (optional) — preferred time, motivation, or barrier.
> 9. **Social proof** — 1–2 short testimonials + a stat.
> 10. **Personalised plan** — restates answers + proposes a setup (e.g.
>     "Daily 5-min sessions, mornings, focus on streaks").
> 11. **Permission priming** — notifications via `KitPermissionPrimer`.
> 12. **Auth nudge** — only if `KitConfig.AUTH_ENABLED`. Frame as "save your
>     plan" rather than "create account".
> 13. **Ready** — "You're all set" + Start CTA → routes to the kit's next
>     gate.

Skip step 12 if `AUTH_ENABLED` is false. If `PAYWALL_ENABLED` is true, the
Ready CTA falls through to the kit's existing Paywall gate — do NOT generate
a paywall screen here; the nav host already handles it.

Show the proposed flow to the developer as a bulleted list (screen name +
one-line purpose). They approve / rename / add / remove before you generate.

## Step 3 — Question design

For each question screen, design:
- A short title (one sentence).
- 3–4 multi-choice answers OR a numeric input OR a free-text input.
- Which DataStore key the answer lands in.

Multi-choice answers should be exclusive (radio-style); allow multi-select
only if the question genuinely has multiple correct answers ("Which of these
matter to you?"). Use `KitChip` for option chips.

Show every question + its answer set to the developer. They approve before
code generation.

## Step 4 — Generate

Generate all of:

1. **Compose screens** under `app/src/main/java/<basePackage>/feature/onboarding/<screen>/`.
   Use `KitTheme.spacing.*`, `KitTheme.icons.*`, `KitButton`, `KitCard`,
   `KitChip`, `KitFeatureRow`. Do not invent new design-system components.

2. **Routes** — append the new screen routes inside a `sealed interface
   OnboardingRoute : Route` block in `Route.kt`, OR add them as top-level
   `data object Onboarding<Name>` entries — match the existing `Route.kt`
   shape rather than imposing a new pattern. Keep `Route.Onboarding` as the
   entry point so the kit's nav resolver does not need changes.

3. **Navigation** — wire the chain inside `KitNavHost`. The chain ends with
   `settings.setOnboardingDone(true)` followed by `navController.navigate(...)`
   to the kit's next gate (the existing `afterOnboarding` resolver returns
   the correct route).

4. **DataStore answers** — extend `SettingsRepository` with one Flow + setter
   per question, named `onboardingAnswer<Goal|Frequency|...>`. Persisted on
   each "Continue" tap. Other screens can later read these to personalise
   defaults.

5. **Strings** — every visible string goes into
   `app/src/main/res/values/strings.xml` with a feature-prefixed name
   (`onboarding_q_goal_title`, `onboarding_q_goal_option_streak`, etc.). Do
   NOT write to any `values-XX/` locale files — the kit ships English-only
   and `/kit-translate` handles other languages later. Do NOT bring up any
   other locales to the developer.

6. **Old OnboardingScreen** — either delete it or convert it into a thin
   wrapper that hosts the new chain. `Route.Onboarding` must still be the
   entry point so `KitNavHost` does not need to change beyond the inner
   chain.

## Step 5 — Compile + report

Run `/kit-compile-app`. Fix any errors. Then summarise:
- Number of new screens + their names.
- New DataStore keys added to `SettingsRepository`.
- "Run `/kit-run-app` to see the flow end-to-end."

Do NOT touch Auth, Paywall, Settings, Home, or any other feature. This skill
is confined to: `feature/onboarding/`, `Route.kt`, `KitNavHost.kt`,
`SettingsRepository.kt`, and `values/strings.xml`.

## Notes for the model

- Onboarding is the FIRST impression — the developer cares a lot. Show every
  proposed screen + every question BEFORE generating; loop on changes until
  approved.
- The default tone is whatever the developer described in `/kit-start-setup`
  Step 2 (if they ran it). If unknown, ask once for tone, then stay
  consistent across every screen.
- Localise every string — see [[feedback-localize-all-strings]].
- This skill never alters layout once generated — if the developer wants
  changes later, treat it as a fresh run, do not edit in-place.
