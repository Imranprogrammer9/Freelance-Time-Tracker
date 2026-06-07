# The Flow — 0 to Google Play

This is the whole journey on one page: from cloning the kit to a **live listing on the
Google Play Store**. Every step is a command. You answer questions; your AI agent writes
the code, edits the config, and runs the builds.

Read it top to bottom once to see the shape. Then just run the commands — each one walks
you through itself.

```
   CLONE              CONFIGURE                 BUILD                  SHIP
     │                    │                       │                     │
  git clone   →   /kit-start-setup    →   /kit-design-app    →   /kit-upload-on-google-play
                  (rename, brand,          (your screens)        (sign, listing, screenshots,
                   auth, paywall,                                 privacy, Data Safety, upload)
                   analytics, build)
```

There are **three phases**: **Set up** the kit, **Build** your app, **Ship** to Play.

---

## Phase 1 — Set up (about 30 minutes)

### Step 1.1 — Check your machine

```
/kit-env-check
```

Confirms JDK 17, Android SDK, `adb`, and the Android tools are installed. Prints the exact
install command for anything missing, for your OS. Skip this only if you've shipped an
Android app from this machine before.

### Step 1.2 — Run the master setup

```
/kit-start-setup
```

This is the big one. It asks **what you're building** (free / paid / just exploring) and
then walks every step, skipping what you don't need:

| Step | What it does | Command behind it |
|------|--------------|-------------------|
| Rename | Your package, applicationId, app name | `/kit-change-app-id` |
| Onboarding | One sentence → 3 intro screens | *(inline)* |
| Brand & theme | Your color + icon pack | `/kit-setup-theme` |
| Authentication | Supabase / Firebase / none | `/kit-setup-auth` |
| Paywall | RevenueCat hard/soft (free apps skip) | `/kit-setup-paywall` |
| Analytics | PostHog / Firebase / Crashlytics / Sentry | `/kit-setup-analytics` |
| Build & run | Compiles, installs, launches | `/kit-run-app` |

**At the end of this step you have your own app** — renamed, branded, configured — running
on your phone. Not a demo. Your app.

> Each sub-step is also a standalone command. Stopped halfway? Just run the single command
> later (e.g. `/kit-setup-paywall`). Re-running is safe — commands detect what's already
> configured and offer to keep it, change one thing, or redo that piece.

### Step 1.3 — Optional extras (only if your app needs them)

Run any of these when the moment comes — none are required to ship:

- `/kit-setup-ai` — turn on AI features (OpenRouter, one key, 100+ models)
- `/kit-design-onboarding` — swap the 3-page intro for a personalised quiz flow
- `/kit-translate` — add languages (RTL, Asia, Europe buckets)
- `/kit-setup-updates` — remote config + force/soft update gate + maintenance mode
- `/kit-setup-review-dialog` — ask for a Play review at a trigger you choose

---

## Phase 2 — Build your app

The kit gives you every feature; now you build the screens that make it **your** app.

### Step 2.1 — Design and wire your screens

```
/kit-design-app
```

This runs in **two phases**, deliberately:

1. **Phase 1 — Layout.** You describe your screens (or point at a Stitch design /
   screenshots / plain text). The kit generates Compose screens using your theme + the kit's
   components, with **dummy data**, wired into navigation. It **stops** so you approve the
   look on your device first.
2. **Phase 2 — Data.** Screen by screen, you pick where data comes from — **Supabase**,
   **Room** (on-device), **Retrofit** (your API), or static. The kit builds the repository,
   the Koin binding, the SQL migration (for Supabase), the ViewModel, and swaps dummy data
   for real state. **It never touches your approved layout.**

This separation is the trick: you lock the look first, then plumb data without breaking it.

### Step 2.2 — Browse what's already built

On your Home screen, tap **Browse kit components** to open the live **Components Catalog** —
every button, field, card, sheet, and state view rendered in **your** brand color and icon
pack. Use it to see what's available before you build something from scratch.

See the full list: **[Components Catalog](components.md)**.

---

## Phase 3 — Ship to Google Play

When the app does what you want, ship it. The kit handles the boring, error-prone parts of
a Play release for you.

### Step 3.1 — Generate your legal pages

```
/kit-generate-legal
```

Scans your actual code — which SDKs you enabled, which network calls and Supabase tables you
use — asks you 8 plain questions (company name, contact email, jurisdiction, …), and writes:

- `playstore/privacy_policy.md` + `.html` (host the HTML for Play's required public URL)
- `playstore/play_data_safety.md` (the answers for Play's Data Safety form)

Your privacy policy is generated **from your code**, so it actually matches what your app
collects.

### Step 3.2 — Generate screenshots

```
/kit-generate-screenshots
```

Creates Play Store phone screenshots (1080×1920) from your app, ASO-optimised — or you drop
your own PNGs into `playstore/screenshots/`.

### Step 3.3 — Plan release analytics *(optional)*

```
/kit-plan-release-analytics
```

Looks at what you're shipping and sets up a conversion funnel (e.g. onboarding → first
action → retention), **auto-inserting** the tracking calls at the right places.

### Step 3.4 — Upload

```
/kit-upload-on-google-play
```

The big finish. It knows whether this is your **first version** or an **update** and walks
the right path:

**First version:**

| | Step |
|---|------|
| A | Pre-flight check (config, URLs) |
| B | App icon |
| C | Release keystore (create + back up your signing key) |
| D | Register your release SHA-1 (so Google sign-in works in production) |
| E | Screenshots |
| F | Store listing copy (via the `aso-googleplay-listing` skill, or write your own) |
| G | Data Safety form + privacy policy |
| H | Plan release analytics |
| I | Create the Play Console app |
| J | Build the signed AAB |
| K | Upload to Play Console |

**Update:** bump version → release notes → (screenshots if UI changed) → update
RemoteAppConfig if wired → build → upload. Much shorter.

> The upload itself is **manual** in Play Console (Google requires a human for the first
> submission anyway). The command produces the signed `.aab`, all the listing assets in
> `playstore/`, and tells you exactly which buttons to click.

### Step 3.5 — Submit for review

Google reviews your app (usually hours to a couple of days for a first app). Then it's live.

🎉 **You shipped an Android app.**

---

## The whole flow at a glance

```
PHASE 1 — SET UP
  /kit-env-check                 verify machine
  /kit-start-setup               rename + brand + auth + paywall + analytics + run
  (optional) /kit-setup-ai, /kit-translate, /kit-setup-updates, /kit-design-onboarding

PHASE 2 — BUILD
  /kit-design-app                your screens (layout → data)

PHASE 3 — SHIP
  /kit-generate-legal            privacy policy + Data Safety
  /kit-generate-screenshots      Play screenshots
  /kit-plan-release-analytics    funnel (optional)
  /kit-upload-on-google-play     sign + listing + create app + build AAB + upload
```

---

## Not sure which choices to make?

Five real apps show you the exact answers for different kinds of app — free, subscription,
offline, AI, hard vs soft paywall: **[Example Recipes](example-recipes.md)**.

Looking up one command's details? **[Commands Reference](commands.md)**.
