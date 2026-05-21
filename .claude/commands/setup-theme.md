---
description: Set the kit's brand color, theme options, and app icon
argument-hint: [#hexcolor]
---

You are running **`/setup-theme`** for the ShipKaro Android Kit. Goal: make the
app look like the developer's brand.

Audience: first-time mobile developers. Be brief and concrete; you make the
edits, they answer questions.

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

The launcher icon lives in `app/src/main/res/mipmap-*`. Generating a real icon
needs image assets that cannot be authored here. Give the developer the
recommended path: in Android Studio, right-click `res` → New → Image Asset →
Launcher Icons, and point it at their logo file. Only offer to drop in a
brand-tinted placeholder if they explicitly ask.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin` to confirm the color change compiles.

Report success and remind them the brand color is the one knob that reskins the
whole app.
