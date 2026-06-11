---
description: Continuation of kit-upload-on-google-play
---

This continues the **First-version path** of `/kit-upload-on-google-play` from
Step E.

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim**.

## E. Screenshots

Call /kit-generate-screenshots. That command handles the
generate-via-skill OR drop-your-own branch, the Play-Store-correct
dimensions (1080×1920 phone), and the output folder
(`playstore/screenshots/`). Skip its Verify step here — Step J below builds.

Skip this step if the developer already has screenshots in
`playstore/screenshots/` from a previous release that they don't want to
redo.

## F. Store listing copy (ASO)

Call /kit-generate-aso. That command drives the `aso-googleplay-listing`
skill — it derives a keyword strategy, confirms which keyword groups to target,
and writes app name, short + long description tuned for Play SEO. The three
files end up at:

- App name → `playstore/title.txt`
- Short description → `playstore/short_description.txt`
- Long description → `playstore/full_description.txt`

Skip this step if the developer already has listing copy they're
happy with from a previous run.

## G. Data Safety, Privacy policy + Landing page

**G.1 — Legal.** Call /kit-generate-legal. That command scans the active
SDKs + KitConfig, asks the developer the legal questions (company name, contact
email, jurisdiction, GDPR / CCPA / COPPA scope), and writes:

- `playstore/privacy_policy.md` — source of truth.
- `playstore/privacy_policy.html` — the page Play scrapes (needs a public URL).
- `playstore/play_data_safety.md` — filled Play Console form answers.

**G.2 — Landing page (gives you the public privacy URL).** Play requires a
**public** privacy-policy URL — the `.html` has to be hosted somewhere. Offer to
generate a simple landing page that hosts it. Ask the user (wait for their answer):

- **Generate a landing page** (recommended) — Call /kit-generate-landing.
  It builds a static `landing/` site (hero + features + screenshots +
  Play badge + **privacy** + **terms** + contact) reusing the privacy policy
  from G.1. The hosted `…/privacy.html` becomes the URL you paste into Play.
- **I'll just host the privacy file myself** — skip the landing page; host
  `playstore/privacy_policy.html` directly.

After the privacy page is hosted (via the landing page or directly), show:

> **Finish the legal setup in Play Console:**
> 1. Copy your public privacy URL (`…/privacy.html` if you made a landing page,
>    else wherever you hosted `privacy_policy.html`).
> 2. **App content → Privacy policy** → paste that URL.
> 3. **App content → Data safety** → open `playstore/play_data_safety.md`
>    side-by-side and fill the web form.
> 4. Update `KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` to the hosted
>    URLs so Settings → Privacy / Terms open them in-app.

Wait for the developer to confirm the policy is hosted and the Data Safety
form is submitted before continuing.

## H. Plan release analytics

Call /kit-plan-release-analytics (don't ask permission — funnels are
load-bearing for "did this release work?"). That command asks the developer
the release goal, suggests 3–5 events, and wires them into the codebase. Skip
its own Verify step here — Step J below builds.

If the developer types "skip" when asked the release goal, skip this step
and continue.

## I. Create the Play Console app

Manual web step:

> **Create your app in Play Console:**
> 1. Open https://play.google.com/console.
> 2. Click **Create app**.
> 3. Enter app name, default language, **Free / Paid**, accept the
>    declarations.
> 4. Complete the **Set up your app** checklist (target audience, ads, content
>    rating, government apps if relevant, news app if relevant, COVID-19
>    contact-tracing if relevant).

Wait for the developer to confirm the app exists in Play Console.

## J. Build the signed AAB

Run from the project root:

    ./gradlew bundleRelease

The signed Android App Bundle lands at:

    app/build/outputs/bundle/release/app-release.aab

If it fails, the usual cause is the signing env vars not being picked up — open
a new terminal and re-check `echo $RELEASE_STORE_FILE`.

## K. Upload — manual

Show this:

> **Upload your release manually in Play Console:**
> 1. Open your app → **Internal testing → Create new release** (recommend
>    internal testing first; you can promote to Production later from the
>    same screen).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Open `playstore/title.txt`, `playstore/short_description.txt`, and
>    `playstore/full_description.txt` and paste each into the matching Play
>    Console field (Main store listing).
> 4. Upload the PNGs from
>    `playstore/screenshots/` to the
>    **Phone screenshots** section.
> 5. Add release notes (a few short bullets of what's in this version).
> 6. **Save → Review release → Start rollout to Internal testing**.
>
> Once you've smoke-tested it, promote to Production from the same screen.

Then call /kit-upload-on-google-play-part-3 for the wrap-up (the Update path
also lives there for reference).
