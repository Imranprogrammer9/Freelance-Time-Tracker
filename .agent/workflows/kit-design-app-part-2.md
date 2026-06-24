---
description: Continuation of kit-design-app
---

This continues `/kit-design-app` once the screen list is approved — generating the
screens (rest of Phase 1) through Phase 2 (wiring functionality, screen by screen).
You should have done 1A–1B (pick a source, approve the screen list) in the main
command first.

## 1C — Generate the screens

For each approved screen, do all of this:

1. **Compose file** — write `app/src/main/java/<basePackage>/feature/<name>/<Name>Screen.kt`
   where `<name>` is the screen name in lowercase and `<Name>` is PascalCase.

   Read `app/build.gradle.kts` for the current `namespace` to find the base
   package — do NOT hardcode `dev.shipkaro.kit` (the dev may have run
   `/kit-change-app-id`).

2. **Style — branch on the 1A.5 mode:**

   **Reproduce mode (faithful):** reproduce the *source's* design, not a kit
   interpretation. Match its layout, **every element** (kickers, category dots,
   tags, badges, counts, empty states), section order, spacing, type, colors, and
   copy.
   - The kit's design system is your **token source**: colors from `Color.kt` /
     `KitTheme.colors`, spacing from `KitTheme.spacing`, icons from
     `KitTheme.icons`. If the source needs a color the theme lacks (a specific
     neutral, a category/semantic color), **add it** to `Color.kt` (or a
     `KitColors` extension) — do not substitute the nearest kit default.
   - Use a `Kit*` component **only when it already matches** the source element. If
     the design's card / row / chip / field differs, build it from Compose
     primitives (`Row`/`Column`/`Box`/`Surface`/`Text`) styled with theme tokens —
     do NOT force `KitCard`/`KitListItem` and call it close enough.
   - If the source is **HTML/CSS** (e.g. a Stitch export), read it for the exact
     hex colors, spacing, font sizes, and DOM structure — that is the spec.

   **Adapt mode:** use the kit design system directly — `KitTheme.spacing.*`,
   `KitTheme.icons.*`, `KitButton`, `KitCard`, `KitListItem`, `KitTextField`,
   `KitBanner`, `KitDialog`, `KitBottomSheet`, `KitChip`, `Scaffold` + `TopAppBar`
   for top-level chrome. Brand color flows from `Color.kt`. Treat the source as
   layout + vibe reference.

3. **Dummy data** — hardcode a small in-file `private val sample…` of realistic
   placeholder data so the screen renders something useful when previewed.
   Items must be plausible for the app concept (not just "Item 1, Item 2"),
   but DO NOT pull from real backends yet.

4. **Stub callbacks** — every action (click, swipe, navigation) is a `() -> Unit`
   parameter on the composable. Inside, just log nothing or no-op (no
   navigation calls yet — that comes when you wire to KitNavHost).

5. **Strings** — every visible string goes into `app/src/main/res/values/strings.xml`
   with a feature-prefixed name (e.g. `feed_title`, `detail_action_share`).
   Add an XML comment block (`<!-- Feed -->`) for grouping. Do NOT translate
   yet — `/kit-translate` handles that later.

6. **Route** — append `@Serializable data object <Name> : Route` to `Route.kt`.

7. **Wire into KitNavHost** — add `composable<Route.<Name>> { <Name>Screen(…) }`
   with the navigation callbacks supplied. Navigation between screens is what
   you write here; the screen itself just accepts the callbacks.

8. **Start screen** — if this is the screen the developer picked as Home in
   step 1B:
   - REPLACE `HomeScreen.kt`'s body with this screen's content, OR
   - delete the old `HomeScreen` route and re-point `Route.Home` (the
     KitNavHost start destination after auth/paywall) at the new screen.
   Whichever you do, keep the auth/paywall gating logic in `KitNavHost`
   unchanged.

Do every screen in one pass — do not stop between screens unless you hit a
build error.

## 1C.5 — Faithfulness pass (Reproduce mode only)

Skip in Adapt mode. In Reproduce mode, before compiling, **diff each generated
screen against its source and fix the gaps** — drift is invisible until it's on a
device, and that costs a full Phase 1 redo.

1. Put the source (the image, or the HTML/CSS) next to your generated screen.
2. Check element by element:
   - Is **every** element present? (kickers, category dots, due/overdue tags,
     badges, counts, section headers, empty states.)
   - Layout + section **order** match the source?
   - Copy matches (not paraphrased)?
   - Colors + spacing pulled from the right tokens (and any source-specific colors
     added to the theme)?
   - Nothing **invented** that isn't in the source (e.g. a "recently captured"
     list), nothing **swapped** (e.g. a progress bar where the source had an
     "N today" pill)?
   - The **signature screen** (1B) reproduced in full, including any overlay /
     full-screen entry behavior?
3. List every deviation, then fix it. Only move on when the screen is a faithful
   match — not a kit-flavored interpretation.

## 1D — Compile + show

After all screens are generated, call /kit-compile-app (`.gradlew :app:compileDebugKotlin`).
If it fails, fix the error before reporting — do not hand a broken graph to the
developer.

Then summarise:
- The screen files you wrote (paths).
- The route map (Home → which screen, and any other screen-to-screen edges).
- A reminder to run `/kit-run-app` to see Phase 1 on a device.

**Stop here.** Wait for the developer to look at the running app and approve
the layouts before starting Phase 2. They will likely want changes —
re-renaming, re-arranging, polishing. Loop on 1B / 1C until they say "looks
good, let's wire it up."

---

# Phase 2 — Wire functionality, screen by screen

Phase 2 runs **one screen at a time**, in the order the developer cares about
most (usually Home first, then whatever Home links to). Do NOT batch Phase 2
across all screens — each screen needs its own design conversation.

For each screen, repeat the loop below.

## 2A — Define the data + actions for this screen

Ask the developer:
- **What does this screen show?** List the data fields visible on screen.
- **What actions does the user take here?** List interactions (taps, form
  submits, swipes, refreshes).
- **Where does the data come from?** Ask the user (wait for their answer):
  - **Supabase** — a Postgres table. You generate SQL the developer pastes
    into Supabase SQL Editor, plus a `<Name>Repository.kt` using `supabase.from(...)`.
  - **Room (local-only)** — an offline-first store. You generate `<Entity>.kt`,
    `<Name>Dao.kt`, register it on `KitDatabase`, and write a
    `<Name>Repository.kt` wrapping the DAO.
  - **REST API** — a backend endpoint over Retrofit. You generate
    `<Name>Api.kt` (interface), models, and a repository.
  - **Static / derived** — no backend; screen reads from in-memory state.

  The auth provider matters: if `KitConfig.AUTH_PROVIDER = SUPABASE`, prefer
  Supabase for new tables (reuses the configured client). Mention this when
  proposing.

## 2B — Generate the data layer

Based on the picks in 2A, write all of:

- **Domain model** — a `data class` in `feature/<name>/data/` (or `core/data/`
  if shared across features).
- **Source** — DAO (Room), Repository (Supabase / Retrofit), or whatever
  matches. Make it injectable.
- **Koin module** — append the binding to `AppModules.kt` in the right module
  (`dataModule` for Room, a feature module for screen-specific repositories).
- **Supabase SQL** — if Supabase, write the `CREATE TABLE …` statement to
  `playstore/../sql/<name>.sql`. Actually — keep release assets in `playstore/`,
  so put SQL migrations at `supabase/migrations/<NNNN>_<name>.sql` at repo
  root (create the dir). Show the SQL to the developer and ask them to run
  it in Supabase SQL Editor before continuing.

## 2C — Generate the ViewModel

Write `feature/<name>/<Name>ViewModel.kt`:
- Constructor-injects the repository.
- Exposes a `StateFlow<UiState>` for screen state (loading / empty / error /
  loaded).
- Exposes intent functions matching the actions from 2A.
- Register it in the feature's Koin module via `viewModel { … }`.

## 2D — Wire VM into the screen

Edit `<Name>Screen.kt` from Phase 1:
- Add `vm: <Name>ViewModel = koinViewModel()` parameter.
- Replace `sample…` dummy data with `vm.state.collectAsState()` reads.
- Replace stub callbacks with VM intent calls.

DO NOT change the visual layout from Phase 1 — only swap the data source.

## 2E — Compile + run

Call /kit-compile-app. Fix errors. Then tell the developer:
- What backend wiring is now live.
- Any manual steps (Supabase SQL to run, env vars to add).
- "Run `/kit-run-app` and tap through this screen on a device."

Wait for the developer to approve before moving to the next screen.

---

## Notes for the model

- **Never skip Phase 1 → Phase 2 sequencing.** Wiring data into an
  unapproved layout wastes time when the layout changes.
- **Never re-generate Phase-1 screens during Phase 2.** Only swap data
  sources. If the developer wants a layout change, treat it as a Phase 1
  revision and come back to Phase 2.
- **Localise every string immediately** — even Phase 1 dummy data goes through
  `strings.xml`. See [[feedback-localize-all-strings]].
- **Respect existing flow.** `KitNavHost` already gates Splash → Onboarding →
  Auth → Paywall before Home. The screens you build live in the post-Paywall
  region and reach each other via `Route.<Name>` entries.
- **One screen at a time in Phase 2.** Resist the urge to bulk-wire — the
  developer's mental model is one screen per conversation.
