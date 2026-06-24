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
  Phase 1 and walk the whole flow.
- **Custom screens exist, none wired** → Phase 1 is done. Briefly "Welcome back",
  show the screen list, confirm it still matches what they want, then resume at
  **Phase 2** for the first screen.
- **Some wired, some not** → resume **Phase 2** at the first un-wired screen. Show a
  ✅ wired / ⬜ to-wire list and ask the user (wait for their answer) which screen to
  wire next (default: first ⬜).

On a resume, show a short ✅ done / ⬜ to-do status list to match — Phase 1 done,
plus each custom screen ✅ wired or ⬜ to wire. Then continue from the resume point.
**Never re-generate a Phase-1 screen that already exists** (see Notes) — only wire it.

## Pre-flight

> **Resumed into Phase 2?** The kit defaults are already in place — skip the
> Phase-1 framing below, but still call `/kit-compile-app` once to confirm the graph
> is green before wiring.

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
sheet for adding") and confirm with the developer before you build. In
**Reproduce** mode (1A.5) treat what you see as the **spec to match**, not just a
vibe.

If the developer already ran `/kit-start-setup` and answered the "what is your
app about" question, reuse that for context when you're proposing screens.

## 1A.5 — Reproduce or adapt?

This sets how faithful the build is to the source — and it changes how **every**
screen is generated in 1C, so decide it now.

Ask the user (wait for their answer) **only when the source is a concrete design**
(Stitch, Figma, or screenshots/mockups). For **Text descriptions** or **From
scratch** there is nothing to reproduce — skip the question and use **Adapt**.

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

---

Once the developer has approved the screen list (1B), call /kit-design-app-part-2 to generate the screens (1C–1D) and wire functionality (Phase 2).
