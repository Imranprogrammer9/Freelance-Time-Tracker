---
description: Remove the bundled demo app so only your own app remains
---

You are running **`/make-it-yours`** for ShipKit. Goal: strip the bundled demo
(a Habit Tracker app) so the kit becomes a clean base for the developer's own
app.

Audience: first-time mobile developers. Be brief and careful — this deletes code.

**Docs:** https://kit.shipkaro.dev/docs/make-it-yours

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## First — confirm what they want

The demo is genuinely useful while learning: it shows onboarding → auth → paywall
→ settings wired together with real components and real data. Ask
(AskUserQuestion):
- **Hide the demo for now** — set `SAMPLE_FEATURE_ENABLED = false` in
  `KitConfig.kt`. The "Launch demo" button on `HomeScreen` disappears, but the
  demo code stays. Fully reversible.
- **Remove it completely** — delete the demo code and unwire it from the kit's
  navigation. Recommend this once they are ready to build their own app.

If they choose **"Hide for now"**, set the flag, confirm, and stop.

## Full removal

Check `git status` first — recommend a clean tree so the deletion is reviewable
and revertible. Warn if there are already uncommitted changes.

Show the developer exactly this:

> **Removing the demo — the 6 changes I'll make:**
> 1. `KitConfig.kt` — set `SAMPLE_FEATURE_ENABLED = false`.
> 2. `Route.kt` — remove `Route.Demo`.
> 3. `KitNavHost.kt` — remove the `composable<Route.Demo> { DemoNavHost() }`
>    block, the `DemoNavHost` import, and pass `onLaunchDemo = {}` (or drop
>    it) in the `HomeScreen` call.
> 4. `HomeScreen.kt` — drop the `onLaunchDemo` parameter and the
>    `KitConfig.SAMPLE_FEATURE_ENABLED` "Launch demo" button.
> 5. `AppModules.kt` — remove the `dev.shipkaro.kit.feature.demo.demoModule`
>    import and the `if (KitConfig.SAMPLE_FEATURE_ENABLED) add(demoModule)`
>    line.
> 6. Delete the whole `feature/demo/` directory — it is self-contained (its
>    own Room database, repository, and screens), so deleting it breaks
>    nothing else.

Then make those six changes yourself.

## Verify

**Skip the build if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If it fails, the likely cause is a leftover reference to a `feature/demo/` symbol
or `Route.Demo` — find and fix those. Report what was removed.
