---
description: Wire Play in-app review prompt at a chosen trigger
---

You are running **`/kit-setup-review-dialog`** for NowKit. Goal: pick a trigger and
call `InAppReviewManager.requestReview(activity)` at that point so the user sees
Google Play's in-app rating sheet without leaving the app.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/in-app-review

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## How this differs from Settings → Rate

`InAppReviewManager` and `PlayStoreLauncher` are two separate paths and the kit
ships both:

| Path | Where | Behaviour |
|------|-------|-----------|
| Settings → Rate | `PlayStoreLauncher.openListing()` | Opens the Play Store listing. Always works. User sees other reviews + can rate. |
| Smart prompt | `InAppReviewManager.requestReview(activity)` | Shows Play's in-app review sheet **inside the app**. Quota-limited by Google — may silently no-op. Best for delight moments. |

This command wires the **smart prompt** at the moment the developer chooses. The
Settings row stays on the listing path either way.

Tell the developer up-front:

> **Heads up on Play quota:** Google limits how often the in-app review sheet shows
> (~3–4 times a year per user, with cool-downs after each appearance). Don't pick a
> trigger that fires every launch — Play will silently no-op most of them. Pick a
> moment where the user just finished something good (saved a note, completed a
> workout, finished onboarding).

## Step 0 — Detect existing state

Before walking the full setup, check whether the prompt is already wired:

1. Grep `app/src/main/java` for `InAppReviewManager.requestReview(` — that's
   the call-site that fires the prompt. Exclude matches inside
   `InAppReviewManager.kt` itself (the implementation file).
2. Read `SettingsRepository.kt` — note whether `reviewPrompted` (and / or
   `launchCount`, `firstLaunchAt`) keys exist. Those are written by previous
   runs of this command.

Branch:

- **Call-site found + DataStore keys exist** — the prompt is already wired.
  Ask the user (wait for their answer):
  - **Keep as-is** (recommended) — exit without changes.
  - **Move the trigger to a different call-site** — walk Step 1 again
    (pick a new trigger style) and remove the old `requestReview(...)`
    call-site after wiring the new one.
  - **Reset the `reviewPrompted` flag** (lets the prompt fire again on next
    launch — useful for testing) — write a one-shot
    `settings.markReviewPrompted(false)` snippet the dev can paste once,
    then revert.
- **Call-site found but DataStore keys missing** — odd state; offer to add
  the DataStore keys without changing the call-site.
- **Nothing wired yet** — walk the full flow below.

## Step 1 — Pick the trigger

Ask the user (wait for their answer). Offer these options:

- **After Nth launch** — show after the app has been opened N times. Most common.
  Default N = 5.
- **After a key action** — fire after the user does something specific (saves a
  note, completes a workout, finishes onboarding). Most effective.
- **After a time delay** — fire after the user has used the app for X days since
  first install. Slowest path; works for habit apps.
- **Manual call site** — don't pick anywhere; the developer wires
  `requestReview(activity)` themselves later.

Branch on the answer.

## Step 2A — After Nth launch

1. Ask N (default 5).
2. Make sure `SettingsRepository` has a launch counter. Open
   `app/src/main/java/dev/shipkaro/kit/core/data/settings/SettingsRepository.kt`.
   If a `launchCount` key + `incrementLaunchCount()` + `launchCount: Flow<Int>`
   don't exist, add them:

   ```kotlin
   private val LAUNCH_COUNT = intPreferencesKey("launch_count")

   val launchCount: Flow<Int> = dataStore.data.map { it[LAUNCH_COUNT] ?: 0 }

   suspend fun incrementLaunchCount() {
       dataStore.edit { it[LAUNCH_COUNT] = (it[LAUNCH_COUNT] ?: 0) + 1 }
   }

   suspend fun markReviewPrompted() {
       dataStore.edit { it[REVIEW_PROMPTED] = true }
   }

   val reviewPrompted: Flow<Boolean> = dataStore.data.map { it[REVIEW_PROMPTED] ?: false }

   private val REVIEW_PROMPTED = booleanPreferencesKey("review_prompted")
   ```

3. In `KitNavHost` (or whichever composable hosts the post-auth start
   destination), increment + check on first composition. Pseudocode the developer
   sees:

   ```kotlin
   val settings = koinInject<SettingsRepository>()
   val reviewManager = koinInject<InAppReviewManager>()
   val context = LocalContext.current

   LaunchedEffect(Unit) {
       settings.incrementLaunchCount()
       val count = settings.launchCount.first()
       val already = settings.reviewPrompted.first()
       if (!already && count >= N) {
           context.findActivity()?.let { act ->
               reviewManager.requestReview(act)
               settings.markReviewPrompted()
           }
       }
   }
   ```

   You wire this in the actual `KitNavHost.kt` — N is the developer-chosen
   number; replace `findActivity()` with whatever helper the file already has
   (LocalActivity / cast LocalContext to Activity).

## Step 2B — After a key action

1. Ask **which action** triggers the prompt. Examples: "saving a note", "finishing
   onboarding", "completing a workout".
2. Use Grep to find the call site (the composable with the relevant button /
   handler — search for the strings the dev mentioned, or the obvious composable
   name). If you find 1 match, use it. If you find 0 or 3+, ask the developer to
   confirm the file path before editing.
3. Insert the prompt + a flag check so it only fires once:

   ```kotlin
   onClick = {
       saveNote()
       scope.launch {
           if (!settings.reviewPrompted.first()) {
               context.findActivity()?.let { act ->
                   reviewManager.requestReview(act)
                   settings.markReviewPrompted()
               }
           }
       }
   }
   ```

4. Add `reviewPrompted` + `markReviewPrompted` to `SettingsRepository` if not
   present (same snippet as 2A).

## Step 2C — After a time delay

1. Ask X (days since first install). Default 3.
2. Add `firstLaunchAt: Flow<Long>` (epoch millis) to `SettingsRepository` if
   missing — record `System.currentTimeMillis()` once on first run.
3. In `KitNavHost`, gate on `(now - firstLaunchAt) >= X.days.inWholeMillis` AND
   `!reviewPrompted`. Same call shape as 2A.

## Step 2D — Manual call site

Show the developer:

> Call this from anywhere you hold an `Activity`:
>
> ```kotlin
> val reviewManager = koinInject<InAppReviewManager>() // or get<…>() from a VM
> scope.launch { reviewManager.requestReview(activity) }
> ```
>
> `InAppReviewManager` is already registered in Koin (see `AppModules.kt`).
> Nothing else to wire.

Make no code edits; just print this snippet.

## Step 3 — Verify

**Skip this step if you are running as part of another orchestrator.** Otherwise
run `./gradlew :app:compileDebugKotlin`.

Report:
- Which trigger was picked.
- What file(s) you touched.
- A reminder that the in-app review sheet only shows for apps **installed from
  the Play Store** — debug builds use `FakeReviewManager` and show nothing on
  screen. Test the trigger by uploading to an internal test track.
