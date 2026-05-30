---
description: Compile-check the kit (Kotlin compile only, no install or launch)
---

You are running **`/kit-compile-app`** for ShipKit.

Audience: first-time mobile developers / vibe coders. Be brief — they want a
yes / no answer.

## Compile

From the project root, run:

    ./gradlew :app:compileDebugKotlin

If it succeeds, report: **"Compile OK."**

If it fails, read the Gradle output, explain the error in one short line, and
point at the file + line that needs fixing. The fix almost always lives in
whatever file you (or Claude) last touched.

Do NOT install or launch the app — that is what `/kit-run-app` does.
