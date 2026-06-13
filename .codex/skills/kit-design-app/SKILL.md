---
name: kit-design-app
description: Design and build your app's screens — two phases, first the UI with dummy data, then the functionality screen-by-screen
---
You are running **`/kit-design-app`** for NowKit.

Goal: turn the developer's design ideas (Stitch, Figma, screenshots, or plain
text) into a working set of Compose screens, then wire data + actions into each
screen one at a time. Two strict phases:

1. **Phase 1 — Screens with dummy data.** Build the full screen list as static
   composables (hardcoded data, stub callbacks). Get the whole flow approved
   before moving to Phase 2.
2. **Phase 2 — Functionality, screen by screen.** For each screen, define the
   data sources and actions, generate the ViewModel + Repository, and wire it
   in. Move to the next screen only after the current one is approved.

Audience: first-time mobile developers / vibe coders. They do NOT write code —
you do. They describe what they want and approve / redirect.

## Pre-flight

Before starting:
- Call /kit-compile-app once. If it fails, fix the build before continuing —
  design is pointless on a broken codebase.
- The kit's default nav flow already gives Splash → Onboarding → Auth → Paywall
  → Home. **Your screens live downstream of Home**, or replace `HomeScreen` as
  the post-Paywall start destination. Do NOT touch Splash / Onboarding / Auth /
  Paywall.

---

# Phase 1 — Screens with dummy data

## 1A — Pick a design source

Ask the user (wait for their answer):
- **Stitch project** — paste your Stitch project URL or ID. Uses the Stitch MCP
  tools (`mcp__stitch__*`) to import the screen list and generate Compose.
- **Figma URL** — paste a Figma file or Figma Community URL. If a Figma MCP /
  Dev Mode connection is available, read the frames directly. Otherwise, tell
  the developer to open the file → select each frame → **Export as PNG** (1×–2×)
  → drop the PNGs in the chat, and design from those. A Community file is a
  great starting point — they can duplicate it to their own Figma first if they
  want to tweak it.
- **Screenshots / mockups** — paste images of each screen (Figma exports,
  Dribbble shots, sketches, photos of paper, whatever). You read the images and
  design from them.
- **Reference URL** — paste a link to a design you like (a Dribbble shot, a real
  app's store page, any inspiration). Treat it as a style + layout reference,
  not a pixel-perfect spec — adapt it to NowKit's components and the developer's
  brand color.
- **Text descriptions** — the developer describes each screen in plain English,
  no visuals.
- **From scratch** — the developer describes the app in one sentence; you
  propose the screen list yourself based on the app concept.

When the source is an image or a reference URL, **call out the key UI patterns
you see** (e.g. "a big circular progress ring", "color-coded cards", "a bottom
sheet for adding") and confirm with the developer before you build — so the
generated screens match the vibe they picked.

If the developer already ran `/kit-start-setup` and answered the "what is your
app about" question, reuse that for context when you're proposing screens.

## 1B — Build the screen list

Based on the source, produce a short bulleted list of screens you intend to
create — name + 1-line purpose each. Examples:

> - **Feed** — vertical list of items the user has saved.
> - **Detail** — full view of one item with actions.
> - **Create** — form to add a new item.
> - **Filter** — modal to narrow the feed.

Ask the developer to **approve, add, remove, or rename** before you generate
anything. Loop until they approve.

Also ask: **which of these is the start screen** (the post-Paywall entry,
replacing the current `HomeScreen` placeholder)?

**Onboarding is NOT part of this command — do not ask about it here.**
`/kit-design-app` is about the app's *own* screens; stay focused on those and go
straight to generating them. The kit already ships a default 3-page
`OnboardingScreen`, and a personalised Calm/Headspace-style questionnaire is a
**separate** step the developer runs when they want it, via
**`/kit-design-onboarding`**. Don't branch the screen-design flow into onboarding;
at most mention `/kit-design-onboarding` once in the final wrap-up.

## 1C — Generate the screens

For each approved screen, do all of this:

1. **Compose file** — write `app/src/main/java/<basePackage>/feature/<name>/<Name>Screen.kt`
   where `<name>` is the screen name in lowercase and `<Name>` is PascalCase.

   Read `app/build.gradle.kts` for the current `namespace` to find the base
   package — do NOT hardcode `dev.shipkaro.kit` (the dev may have run
   `/kit-change-app-id`).

2. **Style** — use the NowKit design system: `KitTheme.spacing.*`,
   `KitTheme.icons.*`, `KitButton`, `KitCard`, `KitListItem`, `KitTextField`,
   `KitBanner`, `KitDialog`, `KitBottomSheet`, `KitChip`, `Scaffold` +
   `TopAppBar` for top-level chrome. Brand color flows from `Color.kt`.

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
