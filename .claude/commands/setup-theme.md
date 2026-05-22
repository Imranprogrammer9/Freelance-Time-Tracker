---
description: Set the kit's brand color and app icon
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

Tell them the light and dark color schemes both derive from this single value;
for a fully hand-tuned palette they can later edit the other values in the same
file. Do not auto-edit those other values.

## Step 2 — App icon

A launcher icon needs image assets that cannot be generated here. Point the
developer to a free online generator. Show them exactly this:

> **Make your app icon online:**
> 1. Go to https://icon.kitchen
> 2. Upload your logo (or pick an emoji / text) and set the background color.
> 3. Click **Download** — you get a ZIP containing `res/mipmap-*` folders.
> 4. Unzip it and copy those `mipmap-*` folders into `app/src/main/res/`,
>    replacing the existing ones.

The kit ships a placeholder icon — fine to leave for now and do this later.

## Step 3 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin` to confirm the color change compiles.

Report success and remind them the brand color is the one knob that reskins the
whole app.
