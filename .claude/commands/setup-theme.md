---
description: Set the kit's brand color, theme options, and app icon
argument-hint: [#hexcolor]
---

You are running **`/setup-theme`** for ShipKit. Goal: make the app look like the
developer's brand.

Audience: first-time mobile developers. Be brief and concrete; you make the
edits, they answer questions.

**Docs:** https://kit.shipkaro.dev/docs/theme

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Brand color

The whole app reskins from one color. Find `Color.kt` (search for a file named
`Color.kt` under `app/src/main/java`, in the `...designsystem.theme` package).
The line to change is:

    val BrandPrimary = Color(0xFF7C3AED)

Ask the developer for their brand color as a hex value (e.g. `#7C3AED`). If they
passed one in `$ARGUMENTS`, use that. Convert `#RRGGBB` into the
`Color(0xFFRRGGBB)` form and replace the `BrandPrimary` value.

Tell them the light and dark color schemes derive from this single value; for a
fully hand-tuned palette they can later edit the other values in the same file.
Do not auto-edit those other values.

## Step 2 — Dynamic color (Material You)

Find `Theme.kt` in the same package. The theme composable has a parameter
`dynamicColor: Boolean = false`. Explain: when true, on Android 12+ the app
adopts the user's wallpaper colors instead of the brand color. The kit ships
`false` so branding stays consistent across devices. Ask if they want it on;
flip the default only if they say yes.

## Step 3 — App icon

A real launcher icon needs image assets that cannot be generated here. Show the
developer exactly this:

> **Set your app icon in Android Studio:**
> 1. Right-click the `res` folder → New → Image Asset.
> 2. Choose **Launcher Icons (Adaptive and Legacy)**.
> 3. Set the **Foreground Layer** to your logo image (or text).
> 4. Set the **Background Layer** to your brand color.
> 5. Click **Next → Finish** — it overwrites the `mipmap-*` folders.

The kit ships a placeholder icon. Only offer to drop in a brand-tinted
placeholder yourself if the developer explicitly asks.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin` to confirm the color change compiles.

Report success and remind them the brand color is the one knob that reskins the
whole app.
