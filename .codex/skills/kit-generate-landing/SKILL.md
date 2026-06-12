---
name: kit-generate-landing
description: Generate a simple static landing page for your app (hero + features + privacy + terms + contact)
---
You are running **`/kit-generate-landing`** for NowKit. Goal: generate a
**simple static HTML + CSS landing page** for the developer's app — no
framework, no build step, no JS dependencies. One folder they can drop on any
static host (GitHub Pages, Netlify, Vercel, Cloudflare Pages) to get:

1. A marketing page for the app (hero + features + screenshots + Play badge).
2. A **Privacy policy** page — the public URL Google Play requires.
3. A **Terms of service** page.
4. A **Contact** section (email + the developer's social links).

Reference style: clean, single-column, calm — like https://www.habitkit.app/.
**Plain hand-written HTML + one CSS file. No React, no Tailwind, no bundler.**

Audience: first-time mobile developers / vibe coders. Be brief; don't paste
walls of text. Everything lands in a new top-level **`landing/`** folder.

This command is **standalone** — run it anytime. `/kit-upload-on-google-play`
Step G offers it inline (the privacy page it produces is the URL Play needs).

## Step 0 — Detect existing state

- If `landing/index.html` already exists — Ask the user (wait for their answer): **Keep as-is**
  (exit), **Regenerate** (overwrite), or **Update content** (re-ask the inputs,
  rewrite). 
- Read what you can reuse so you don't re-ask:
  - **App name** → `app/src/main/res/values/strings.xml` (`app_name`).
  - **Brand color** → `BrandPrimary` in `core/designsystem/theme/Color.kt`.
  - **Screenshots** → any PNGs in `playstore/screenshots/`.
  - **Contact email + company** → if `playstore/privacy_policy.md` exists, pull
    the contact email and company name already used there.
  - **App tagline / features** → reuse the onboarding copy or the
    `/kit-start-setup` "what is your app about" answer if available; otherwise
    ask.

## Step 1 — Gather the few missing inputs

Ask only for what you couldn't reuse. Keep it conversational:

1. **One-line tagline** — the hero subtitle (e.g. "Build habits that actually
   stick").
2. **3–5 features** — short title + one line each. Reuse onboarding/feature copy
   if you already have it; just confirm.
3. **Play Store URL** — `https://play.google.com/store/apps/details?id=<applicationId>`.
   If the app isn't published yet, use that URL anyway (it goes live on
   publish) OR let them pick "no badge yet" and omit the badge.
4. **Contact** — confirm the **email**, then ask for any **social links** to
   show (X/Twitter, WhatsApp, GitHub, website, Instagram — whatever they have;
   all optional). Render only the ones they give.

Do **not** build a contact form or any backend — email + social links only.

## Step 2 — Make sure the legal pages exist

The landing page links to a Privacy policy and Terms of service. Before
generating:

- **Privacy** — if `playstore/privacy_policy.html` exists, reuse its body for
  `landing/privacy.html`. If it doesn't, tell the developer to run
  **`/kit-generate-legal`** first (it scans the actual SDKs and writes an
  accurate policy), then come back — or offer to run it inline now.
- **Terms** — `/kit-generate-legal` leaves ToS as a template. Fill
  `legal/terms-template.md` with the app name, company, and jurisdiction the
  developer gave, and render it into `landing/terms.html`.

## Step 3 — Generate the files

Write a self-contained static site into `landing/`:

```
landing/
├── index.html        ← hero + features + screenshots + Play badge + footer
├── privacy.html      ← privacy policy (from playstore/privacy_policy.html)
├── terms.html        ← terms of service (from the filled template)
├── styles.css        ← one stylesheet, shared by all pages
└── assets/           ← copied screenshots + any icon
```

Requirements for the HTML/CSS you write:

- **One `styles.css`** linked by all three pages. Use the app's `BrandPrimary`
  as the accent color (CSS custom property `--accent`). Light, clean, system
  font stack. Mobile-first, responsive, centered single column (max ~720px).
- **`index.html`** sections, top to bottom:
  - **Hero** — app name, tagline, a primary CTA button linking to the Play
    Store URL (Google Play badge image if you add one to `assets/`, else a
    styled button), and 1–3 phone screenshots from `playstore/screenshots/`
    (copy them into `landing/assets/`).
  - **Features** — the 3–5 features as simple cards or a list.
  - **Contact** — the email as a `mailto:` link + the social links provided.
  - **Footer** — small links to `privacy.html` and `terms.html`, and a
    copyright line with the company name + year.
- **`privacy.html` / `terms.html`** — the legal content inside the same shell
  (same header/footer/styles), with a "← Back" link to `index.html`.
- Accessibility basics: `<title>` per page, `alt` on every image, semantic tags
  (`<header> <main> <section> <footer>`), `lang="en"`.
- **No external requests** — no Google Fonts, no CDN CSS, no analytics, no JS
  unless trivially inline. Everything must work offline from the folder.

Keep the design tasteful and minimal — this is a calm product page, not a
loud SaaS splash.

## Step 4 — Wrap up + hosting

Don't run any build (it's static HTML). Tell the developer:

> Your landing page is in **`landing/`**. Preview it by opening
> `landing/index.html` in a browser.
>
> **To get the public Privacy policy URL Google Play needs:**
> 1. Push the `landing/` folder to a public host — easiest free options:
>    GitHub Pages, Netlify drop, Vercel, or Cloudflare Pages.
> 2. Your privacy URL becomes `https://<your-host>/privacy.html`.
> 3. In Play Console → **App content → Privacy policy**, paste that URL.
> 4. Set `KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` to
>    `…/privacy.html` and `…/terms.html` so Settings → Privacy / Terms open the
>    same pages in-app.

If they want a one-command backup of the whole project (including `landing/`)
to their own GitHub, mention **`/kit-save-to-github`**.
