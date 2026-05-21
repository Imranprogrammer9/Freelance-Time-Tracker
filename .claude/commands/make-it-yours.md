---
description: Remove the bundled demo app so only your own app remains
---

You are running **`/make-it-yours`** for the ShipKaro Android Kit. Goal: strip
the bundled demo (a Habit Tracker app) so the kit becomes a clean base for the
developer's own app.

Audience: first-time mobile developers. Be brief and careful — this deletes code.

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

## Full removal — 3 steps

Recommend a clean git state first (`git status`) so the deletion is reviewable
and revertible. Warn them if there are already uncommitted changes.

1. **Welcome screen** — `WelcomeScreen.kt` (under `feature/welcome/`) has a
   "Launch Demo" button. Remove that button and its navigation callback. This
   screen is a placeholder the developer is expected to replace with their own
   entry screen anyway.

2. **DI module** — in `AppModules.kt`, remove the demo import
   (`import ...feature.demo.demoModule`) and the line
   `if (KitConfig.SAMPLE_FEATURE_ENABLED) add(demoModule)`.

3. **Delete the demo subtree** — delete the entire `feature/demo/` directory. It
   is self-contained (its own Room database, repository, and screens), so
   deleting it breaks nothing else.

Then **rewire navigation**: `KitNavHost.kt` currently routes `Welcome → Demo`.
With the demo gone, `Welcome` should route to the developer's own first screen.
If they do not have one yet, leave `Welcome` as the start destination and tell
them to point it at their screen later.

## Verify

Run `./gradlew :app:compileDebugKotlin`. If it fails, the likely cause is a
leftover reference to a `feature/demo/` symbol or the `Demo` route — find and fix
those. Report what was removed.
