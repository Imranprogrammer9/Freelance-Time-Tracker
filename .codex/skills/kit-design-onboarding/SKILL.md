---
name: kit-design-onboarding
description: Build a personalised multi-screen questionnaire-style onboarding (Calm / Headspace style)
---
You are running **`/kit-design-onboarding`** for NowKit.

Goal: replace the kit's default 3-page intro pager with a personalised
multi-screen questionnaire flow that asks the user goal-based questions and
tailors the app from their answers.

Audience: first-time mobile developers / vibe coders. They want a real
onboarding without writing any code.

## When to run

Run this once the app's core idea is clear — typically after
`/kit-start-setup` and ideally after `/kit-design-app` has produced the main
screens (so this command knows what app it is building onboarding for). It
can also be run standalone if the developer just wants to upgrade the
onboarding without touching the rest of the design.

Re-running on top of an already-built questionnaire regenerates from
scratch — treat it as a redesign.

## How it works

Invoke the `onboarding-questionnaire` skill shipped with this kit (at
`.claude/skills/onboarding-questionnaire/`). The skill:

1. Reads the app and proposes a 10–14 screen flow (welcome → value-prop →
   3–5 question screens → social proof → personalised plan → permission
   priming → auth nudge if enabled → ready).
2. Asks the developer to approve the screen list, each question, and the
   answer choices.
3. Generates Compose screens, routes, navigation wiring, DataStore answer
   storage, and strings — all using the kit's design system.
4. Replaces the existing `OnboardingScreen` while keeping `Route.Onboarding`
   as the entry point so the kit's nav resolver stays unchanged.

## Run it

1. Invoke the **`onboarding-questionnaire`** skill.
2. Follow its prompts — the developer answers, you write the code.
3. When the skill finishes, it reports the new screen list and asks the
   developer to verify on device with `/kit-run-app`.

Stay inside the skill's scope — do NOT touch Auth, Paywall, Settings, Home,
or any other feature. The skill confines its changes to
`feature/onboarding/`, `Route.kt`, `KitNavHost.kt`, `SettingsRepository.kt`,
and `values/strings.xml`.
