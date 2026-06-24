---
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

## Resume check — do this FIRST

This is a two-phase, multi-screen build a developer often returns to across
sessions. Progress lives in the project files (durable), not this chat — so before
anything, read the project to see how far it got:

- **Phase 1 done?** Look under `app/src/main/java/<basePackage>/feature/` and in
  `Route.kt` for screens beyond the kit defaults (home, settings, profile, auth,
  onboarding, paywall, permissions, catalog, licenses). Any *custom* feature screen
  means Phase 1 already generated screens.
- **Phase 2 per-screen?** For each custom screen, check whether it's wired: a
  `<Name>ViewModel.kt` exists AND `<Name>Screen.kt` reads `vm.state` /
  `koinViewModel()` (wired) vs still holding `sample…` dummy data + stub callbacks
  (not wired).

Then branch:

- **Nothing custom yet** (only kit defaults) → fresh build: start at Pre-flight →
  Phase 1, TaskCreate all tasks `pending`, walk the whole flow.
- **Custom screens exist, none wired** → Phase 1 is done. Briefly "Welcome back",
  show the screen list, confirm it still matches what they want, then resume at
  **Phase 2** for the first screen.
- **Some wired, some not** → resume **Phase 2** at the first un-wired screen. Show a
  ✅ wired / ⬜ to-wire list and ask which screen to wire next (default: first ⬜).

On a resume, recreate the task list with **TaskCreate** to match — Phase 1 tasks
`completed`, plus one "Phase 2 — Wire <Screen>" task per custom screen with the
wired ones pre-marked `completed`. Then continue from the resume point. **Never
re-generate a Phase-1 screen that already exists** (see Notes) — only wire it.

## Pre-flight

> **Resumed into Phase 2?** The kit defaults are already in place — skip the
> Phase-1 framing below, but still run `/kit-compile-app` once to confirm the graph
> is green before wiring.

Before starting:
- Run `/kit-compile-app` once. If it fails, fix the build before continuing —
  design is pointless on a broken codebase.
- The kit's default nav flow already gives Splash → Onboarding → Auth → Paywall
  → Home. **Your screens live downstream of Home**, or replace `HomeScreen` as
  the post-Paywall start destination. Do NOT touch Splash / Onboarding / Auth /
  Paywall.

---

## Progress tracking

Before Phase 1 starts, call **TaskCreate** with the five tasks below, all
`pending`. Mark each `in_progress` when you enter it and `completed` when
done. If the developer skips a sub-step (e.g. they say "no design source,
just describe the app" — Phase 1A still completes but with a different
branch), mark the task `completed` and prefix its content with `[skipped] `
via **TaskUpdate**.

Once Phase 1 is approved and the screen list is locked, call **TaskCreate**
again to append one "Phase 2 — Wire <ScreenName>" task per screen. Mark
each `in_progress` when you start wiring that screen and `completed` when the
developer approves it on device.

Initial task titles (verbatim):

- Phase 1A — Pick design source
- Phase 1B — Approve screen list
- Phase 1C — Generate screens
- Phase 1D — Compile + present for approval
- Phase 2 — Wire functionality (per-screen tasks added after Phase 1)

# Phase 1 — Screens with dummy data

## 1A — Pick a design source

Ask (AskUserQuestion):
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
sheet for adding") and confirm with the developer before you build. In
**Reproduce** mode (1A.5) treat what you see as the **spec to match**, not just a
vibe.

If the developer already ran `/kit-start-setup` and answered the "what is your
app about" question, reuse that for context when you're proposing screens.

## 1A.5 — Reproduce or adapt?

This sets how faithful the build is to the source — and it changes how **every**
screen is generated in 1C, so decide it now.

Ask (AskUserQuestion) **only when the source is a concrete design** (Stitch,
Figma, or screenshots/mockups). For **Text descriptions** or **From scratch**
there is nothing to reproduce — skip the question and use **Adapt**.

- **Reproduce my design exactly** — match the source's layout, component anatomy,
  spacing, colors, and copy as closely as Compose allows. The kit's design system
  becomes the **token source** (colors / spacing / type / icons), *not* a
  component mandate: a `Kit*` component is used only where it already matches the
  design; anything else is built from Compose primitives styled with `KitTheme`.
  Pick this when the developer brought a real mockup they care about.
- **Adapt to the kit's look** — rebuild with the kit's design system (Kit*
  components, brand color flowing through) for a consistent, kit-native feel;
  treat the source as layout + vibe reference. Faster, less faithful.

Remember the choice. If the developer brought a detailed mockup and is unsure,
default to **Reproduce**.

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

**Protect the signature screen.** Identify the app's single *defining* experience —
the thing the app is fundamentally *for* (e.g. a full-screen capture overlay, the
main canvas, the core interaction). It is **not** just another row in the list:
never defer it to "later", simplify it away, or drop it from Phase 1. If the
source implies a non-standard entry (an overlay, an immediate full-screen action),
call that out and preserve that behavior. Confirm with the developer which screen
this is.

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

After all screens are generated, run `/kit-compile-app` (`.gradlew :app:compileDebugKotlin`).
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
- **Where does the data come from?** Ask (AskUserQuestion):
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

Run `/kit-compile-app`. Fix errors. Then tell the developer:
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
