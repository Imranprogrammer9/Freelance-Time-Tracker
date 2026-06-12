---
name: kit-plan-release-analytics
description: Plan and wire release-specific analytics events + funnels before a Play upload
---
You are running **`/kit-plan-release-analytics`** for NowKit. Goal: before the
developer ships this release to Google Play, capture the funnel events they need
to learn what's working. Read the unreleased changelog, ask a few targeted
questions about app + release goals, then add `AnalyticsEvents` constants + wire
`analytics.logEvent(...)` calls at the right call sites in the codebase.

Audience: first-time mobile developers / vibe coders. Almost no analytics
literacy assumed. Be brief. **You make the edits — do not hand over snippets
for the developer to paste.**

This command is the strategy step. `/kit-setup-analytics` is the plumbing step
(SDK keys + manager init) and only runs once at project start. They are
deliberately separate.

**Docs:** https://kit.shipkaro.dev/docs/release-analytics

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Verify analytics is wired

Read `app/src/main/java/dev/shipkaro/kit/core/config/KitConfig.kt`. If
`ANALYTICS_ENABLED = false`, stop and tell the dev:

> Analytics is disabled for this build (`KitConfig.ANALYTICS_ENABLED = false`).
> Run `/kit-setup-analytics` first — that wires PostHog / Firebase Analytics
> SDKs. Then come back to this command to plan release events.

Also Grep for `BuildConfig.POSTHOG_API_KEY` use. If `POSTHOG_API_KEY` is empty
in `local.properties` AND no `google-services.json` exists at `app/`, warn:

> Neither PostHog nor Firebase Analytics has credentials. Events will fire from
> code but nothing will reach a dashboard. Add at least one provider before
> shipping.

If only Firebase is present, mention PostHog gives funnels out of the box;
Firebase needs an Audience configured before funnels work.

## Step 2 — Read context

Read these files and form a mental model — do not show their contents to the
developer:

- `CHANGELOG.md` — pull the most recent unreleased section (lines under
  `## [Unreleased]` until the next heading). If empty, ask the dev what's
  shipping in this release.
- `app/src/main/java/dev/shipkaro/kit/core/analytics/AnalyticsEvents.kt` — note
  what events already exist so you don't duplicate.
- `app/src/main/java/dev/shipkaro/kit/Route.kt` — list of screens.
- `app/src/main/res/values/strings.xml` — short skim, infer app domain from
  visible strings.

## Step 3 — Pick release goal

Ask the user (wait for their answer) with three or four options tailored to **what you read in
Step 2** (don't show generic options — derive them from the changelog). For
example:

- If the changelog adds a paywall screen → ask "Is the goal of this release to
  improve conversion, retention, or onboarding completion?"
- If the changelog adds a new feature → ask "Is the goal to drive adoption of
  the new feature, measure engagement, or track failure modes?"
- If the changelog is mostly bug fixes → ask "Is the goal to measure crash-free
  rate, retention, or feature-specific usage you skipped before?"

Capture the answer. Skip Step 4–6 entirely if the dev picks "I just want to
ship — no new analytics this release".

## Step 4 — Suggest events

Based on the release goal + the screens you saw in Step 2, propose **3 to 5
events** that form a funnel. Format each as:

    <CONST_NAME> — fires when … — fields: <param=type>, <param=type>

Examples (your suggestions must be specific to the codebase, not these):

- `WORKOUT_STARTED` — fires when the user taps Start on a workout — fields: `workout_id=string`
- `WORKOUT_COMPLETED` — fires when the user finishes a workout — fields: `workout_id=string`, `duration_seconds=int`
- `PREMIUM_PROMPT_SHOWN` — fires when the soft paywall appears — fields: `trigger=string`
- `PREMIUM_PROMPT_CONVERTED` — fires when the user purchases from that prompt — no fields

Show the list to the developer and ask the user (wait for their answer):
"Approve this set? Or edit?". If they want changes, iterate once. Do not propose
more than 5 — funnel clarity drops sharply past that.

## Step 5 — Wire events into code

This is the hybrid auto-insert step. Do **not** insert blindly into every
plausible onClick — that creates noise. Instead, for each approved event:

1. Use Grep to find the call site. Heuristics by event-name shape:
   - `*_VIEWED` / screen visits → already handled by `analytics.logScreen()` in
     each screen's `LaunchedEffect(Unit)`. If the screen is missing that call,
     add it.
   - `*_STARTED` / `*_TAPPED` → grep for the matching button label string in
     `strings.xml`, find the `Text(stringResource(R.string.X))` callers, locate
     the `onClick = { … }`.
   - `*_COMPLETED` / `*_SUCCESS` → grep for the matching VM function (e.g.
     `WorkoutViewModel.finish()`); insert at the success path of that function
     or in a `.onSuccess { }` block.
   - `*_ERROR` / `*_FAILURE` → insert in the matching catch / onFailure path.

2. If Grep finds exactly **1** match, insert without asking. If it finds **0**
   or **3+**, ask the developer to confirm the file + line range before
   editing.

3. Insert pattern for a click handler:

   ```kotlin
   onClick = {
       analytics.logEvent(
           AnalyticsEvents.WORKOUT_STARTED,
           mapOf(AnalyticsParams.WORKOUT_ID to workout.id),
       )
       startWorkout()
   }
   ```

4. Insert pattern for a VM success path:

   ```kotlin
   repository.finishWorkout(id).onSuccess {
       analytics.logEvent(
           AnalyticsEvents.WORKOUT_COMPLETED,
           mapOf(
               AnalyticsParams.WORKOUT_ID to id,
               AnalyticsParams.DURATION_SECONDS to it.durationSeconds,
           ),
       )
   }
   ```

5. Update `AnalyticsEvents.kt` — append constants under a comment header named
   after the release (e.g. `// Release 1.4 — onboarding revamp`).

6. Update `AnalyticsParams.kt` — append any new param keys (e.g.
   `WORKOUT_ID`, `DURATION_SECONDS`).

7. If the call site's class doesn't have `AnalyticsManager` available, inject
   it. Composable → `val analytics = koinInject<AnalyticsManager>()`. ViewModel
   → add to constructor + the `featureModule` binding in `AppModules.kt`.

## Step 6 — Print dashboard / funnel plan

After wiring, print a verbatim block the developer takes to PostHog (or
Firebase) to build the funnel:

> **Funnel to build in PostHog:**
>
> 1. Open https://app.posthog.com → Insights → New → Funnel.
> 2. Steps (in this order):
>    - Event: `<EVENT_1>`
>    - Event: `<EVENT_2>`
>    - Event: `<EVENT_3>`
> 3. Time window: 7 days. Conversion goal: > 30% (adjust to taste).
> 4. Save as **"<Release name> conversion"**.
>
> **Why these events:** <one sentence on what the funnel reveals>.

If the developer wired Firebase only, swap to instructions for Firebase
Analytics → Funnel exploration.

## Step 7 — Verify

**Skip this step if you are running as part of `/kit-upload-on-google-play`** —
that orchestrator builds at the end. Otherwise run
`./gradlew :app:compileDebugKotlin`.

Report:
- Events added (names).
- Files touched.
- The funnel-build instructions block (so the dev has it scroll-back-able).
