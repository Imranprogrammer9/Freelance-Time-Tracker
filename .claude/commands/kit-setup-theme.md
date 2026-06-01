---
description: Set the kit's brand color and pick the app's icon pack
argument-hint: [#hexcolor]
---

You are running **`/kit-setup-theme`** for ShipKit. Goal: make the app look like the
developer's brand.

Audience: first-time mobile developers. Be brief and concrete; you make the
edits, they answer questions.

**Docs:** https://kit.shipkaro.dev/docs/theme

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 0 — Detect existing state

Before walking the full setup, check what's already configured:

1. Read `Color.kt` — note the current `BrandPrimary` hex value. Default is
   `#7C3AED` (ShipKit purple).
2. Inspect `Theme.kt` for the `LocalKitIcons` provider — note which icon-pack
   impl is wired (Material default / Feather / Tabler / custom).
3. Check `app/build.gradle.kts` for which `composeicons-*` deps are
   uncommented (Feather, Tabler, etc.).

Branch:

- **Both already set to non-default values** — AskUserQuestion:
  - **Keep as-is** (recommended) — exit without changes.
  - **Reconfigure both** — walk Step 1 + Step 2.
  - **Change brand color only** — jump to Step 1.
  - **Change icon pack only** — jump to Step 2.
- **Only one is non-default** — tell the developer which one is already set
  and walk only the missing step.
- **Default values still in place** (brand still purple, Material icons in
  use) — walk the full flow below.

## Step 1 — Brand color

The whole app reskins from one color. Find `Color.kt` (search for a file named
`Color.kt` under `app/src/main/java`, in the `...designsystem.theme` package).
The line to change is:

    val BrandPrimary = Color(0xFF7C3AED)

Ask the developer for their brand color as a hex value (e.g. `#7C3AED`). If they
passed one in `$ARGUMENTS`, use that. Convert `#RRGGBB` into the
`Color(0xFFRRGGBB)` form and replace the `BrandPrimary` value.

### Derive the dark-mode + container variants

`LightPrimary = BrandPrimary` is set automatically, but the Light container
colours and the entire Dark scheme are hardcoded. Without deriving them, dark
mode keeps the old purple even after the brand color changes. Update these
six values in `Color.kt` based on the new brand hex, using the heuristic
below (convert the brand to HSL, then re-emit as `0xFFRRGGBB`):

    val LightPrimaryContainer   = BrandPrimary HSL → lightness ≈ 92%, sat ≈ 60%
    val LightOnPrimaryContainer = BrandPrimary HSL → lightness ≈ 12%, sat ≈ 80%
    val DarkPrimary             = BrandPrimary HSL → lightness ≈ 78%, sat ≈ 70%
    val DarkOnPrimary           = BrandPrimary HSL → lightness ≈ 18%, sat ≈ 70%
    val DarkPrimaryContainer    = BrandPrimary HSL → lightness ≈ 32%, sat ≈ 65%
    val DarkOnPrimaryContainer  = BrandPrimary HSL → lightness ≈ 90%, sat ≈ 55%

Hue stays at the brand's hue throughout. Lightness percentages are targets,
not exact — adjust by ±5% if the brand is already very light (lightness > 70%)
or very dark (lightness < 25%) so contrast stays AA-readable. Always keep
`LightOnPrimary` at white (`Color(0xFFFFFFFF)`) since `LightPrimary` is the
brand at its natural lightness.

Also update `BrandPrimaryBright` to a lighter accent of the brand
(lightness ≈ 70%, sat ≈ 80%). Leave `BrandSecondary` and `BrandTertiary`
alone — they are intentionally complementary accents, not brand-derived.

Do not touch any other values in `Color.kt`. For a fully hand-tuned palette
the developer can edit them later.

## Step 2 — Icon pack

The kit's design system reads icons via a `KitIcons` interface, with the
default pack swappable in one line. Ask (AskUserQuestion) which pack the app
should ship with. Include the preview URL in each option's description so
the developer can browse the style before picking:

- **Material (default)** — Material extended icons. Preview:
  https://fonts.google.com/icons. Already active; safest and most familiar.
- **Feather** — minimal outline icons (~280 icons). Preview:
  https://feathericons.com. Switches default to `FeatherKitIcons`.
- **Tabler** — large outline pack (4000+ icons). Preview:
  https://tabler-icons.io. Switches default to `TablerKitIcons`.
- **Pick another pack** — choose from the optional `compose-icons` packs
  bundled (commented) in the catalog. After picking, Claude wires the pack
  and generates the matching `KitIcons` implementation.

### If they pick Material

Material is already the default. Nothing to wire — go straight to **Apply**
to disable Feather + Tabler so the build slims down.

### If they pick Feather

Update the active default:
- In `core/designsystem/icons/KitIcons.kt`, change
  `val LocalKitIcons = staticCompositionLocalOf<KitIcons> { MaterialKitIcons }`
  to `... { FeatherKitIcons }`.

Then go to **Apply** to disable Material + Tabler.

### If they pick Tabler

Same as Feather — change `LocalKitIcons`'s default to `TablerKitIcons`,
then **Apply** to disable Material + Feather.

### If they pick "another pack"

Ask the developer in plain conversational text which pack they want, listing
the optional packs with their preview URLs so they can compare:

> **Optional `compose-icons` packs** (all commented in
> `gradle/libs.versions.toml`):
> - **Simple Icons** — brand / logo icons (Google, GitHub, etc.). Preview:
>   https://simpleicons.org
> - **Font Awesome** — large general-purpose pack. Preview:
>   https://fontawesome.com/icons
> - **Eva** — flat outline / fill pack. Preview:
>   https://akveo.github.io/eva-icons/
> - **Octicons** — GitHub's icon set. Preview:
>   https://primer.style/foundations/icons
> - **Line Awesome** — outline alternative to Font Awesome. Preview:
>   https://icons8.com/line-awesome
> - **Linea** — minimal line icons. Preview: http://linea.io
> - **Weather** — weather-themed icons. Preview:
>   https://erikflowers.github.io/weather-icons/
> - **CSS GG** — minimal essential icons. Preview: https://css.gg

For their pick:

1. Uncomment the matching `composeicons-<pack>` line in
   `gradle/libs.versions.toml` (under `[libraries]`).
2. Add `implementation(libs.composeicons.<pack>)` to
   `app/build.gradle.kts`, grouped near the other `composeicons-*`
   implementations.
3. Generate
   `app/src/main/java/<basePackage>/core/designsystem/icons/<Pack>KitIcons.kt`
   implementing `KitIcons` — map each of these 32 semantic slots to the
   closest equivalent icon in the chosen pack:

   `back`, `close`, `menu`, `search`, `more`, `arrowRight`, `chevronRight`,
   `add`, `edit`, `delete`, `check`, `share`, `logout`, `account`,
   `settings`, `notification`, `lock`, `email`, `visibility`,
   `visibilityOff`, `google`, `palette`, `language`, `star`, `shield`,
   `update`, `maintenance`, `camera`, `location`, `success`, `info`,
   `warning`, `error`.

   Read `app/build.gradle.kts` `namespace` to derive `<basePackage>` — do
   NOT hardcode `dev.shipkaro.kit` (the dev may have run
   `/kit-change-app-id`).

4. Change `LocalKitIcons`'s default in `KitIcons.kt` to the new impl.
5. Go to **Apply** to disable the original three packs.

### Apply — comment out unused packs (reversible)

To slim the build without losing the option to switch later, for each pack
the developer is **NOT** using:

1. **Catalog** — in `gradle/libs.versions.toml` under `[libraries]`,
   prepend `# ` to the matching line. (Material is
   `androidx-compose-material-icons`; the others are
   `composeicons-feather`, `composeicons-tabler`, etc.)
2. **Build file** — in `app/build.gradle.kts`, prepend `// ` to the
   matching `implementation(libs.<...>)` line.
3. **Implementation file** — in
   `core/designsystem/icons/<Pack>KitIcons.kt`, wrap the entire file
   content after the `package` declaration in a `/* ... */` block comment.
   Example:

       package <basePackage>.core.designsystem.icons

       /* Disabled — uncomment to re-enable this icon pack.
          Also uncomment the matching libs.versions.toml +
          app/build.gradle.kts lines.

       import ...
       object FeatherKitIcons : KitIcons { ... }
       */

   The file stays in the repo, just does not compile.

To switch packs later, the developer (or Claude on a re-run of
`/kit-setup-theme`) reverses the three steps above for the desired pack and
applies them to the now-current pack.

## Step 3 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin` to confirm the color and icon-pack
changes compile.

Report success and remind them the brand color is the one knob that
reskins the whole app, and the icon pack is the one knob that re-styles
every icon kit-wide.
