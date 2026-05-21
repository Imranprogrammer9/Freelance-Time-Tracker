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
- **Keep the demo for now** — just set `SAMPLE_FEATURE_ENABLED = false` in
  `KitConfig.kt` so the demo is off but the code stays. Fully reversible.
  Recommend this if they are still exploring the kit.
- **Remove it completely** — delete the demo code. Recommend this once they are
  ready to build their own app.

If they choose "keep for now", set the flag, confirm, and stop.

## Full removal

Check `git status` first — recommend a clean tree so the deletion is reviewable
and revertible. Warn if there are already uncommitted changes.

Show the developer exactly this:

> **Removing the demo — the 3 changes I'll make:**
> 1. `WelcomeScreen.kt` — remove the "Launch Demo" button and its callback.
> 2. `AppModules.kt` — remove the `demoModule` import and the line
>    `if (KitConfig.SAMPLE_FEATURE_ENABLED) add(demoModule)`.
> 3. Delete the whole `feature/demo/` directory. It is self-contained (its own
>    Room database, repository, and screens), so deleting it breaks nothing.

Then make those three changes.

After removal, **rewire navigation**: `KitNavHost.kt` currently routes
`Welcome → Demo`. With the demo gone, `Welcome` should route to the developer's
own first screen. If they do not have one yet, leave `Welcome` as the start
destination and tell them to point it at their screen later.

## Verify

**Skip the build if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If it fails, the likely cause is a leftover reference to a `feature/demo/` symbol
or the `Demo` route — find and fix those. Report what was removed.
