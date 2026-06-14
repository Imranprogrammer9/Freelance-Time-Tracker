---
name: kit-generate-landing
description: Generate a simple static landing page for your app (hero + features + privacy + terms + contact)
---
You are running **`/kit-generate-landing`** for NowKit. Goal: generate a
**simple static HTML + CSS landing page** for the developer's app — no
framework, no build step, no JS dependencies. One folder, hosted free (Step 4
walks GitHub Pages / Firebase Hosting / Vercel / self-host), to get:

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

## Step 4 — Host it (pick a path)

Don't run a build (it's static HTML). First let them preview:

> Your landing page is in **`landing/`** — preview it by opening
> `landing/index.html` in a browser.

Then host it to get the **public Privacy-policy URL Google Play requires**.
Ask the user (wait for their answer) which path, GitHub Pages first
(recommended). **Include the Firebase Hosting option ONLY if
`app/google-services.json` exists** (i.e. they already configured Firebase for
auth/analytics/etc) — otherwise omit it:

- **GitHub Pages (recommended — I'll do it for you)** — free, no new account,
  reuses the `gh` CLI. Lands in its own **public** repo.
- **Firebase Hosting (I'll guide you)** — *only if Firebase is already set up.*
  Free; deploys onto your existing Firebase project.
- **Vercel (I'll guide you)** — nicer `app.vercel.app` URL + easy custom domain.
- **I'll host it myself** — you have your own domain/host; I just tell you what to
  upload.

### Path A — GitHub Pages (fully automated)

The landing goes in its **own public repo** — keep the app repo private; free
Pages needs a public repo. Confirm `gh` is authed (`gh auth status`; if not,
`gh auth login`). Then, from the project root (substitute `<app>` = the app slug,
`<user>` = their GitHub username from `gh api user -q .login`):

    cd landing
    git init && git add -A && git commit -m "Landing page"
    gh repo create <app>-landing --public --source=. --push
    cd ..
    gh api -X POST repos/<user>/<app>-landing/pages -f 'source[branch]=main' -f 'source[path]=/'

If the Pages API call 404s, tell them to flip it in the UI: the new repo →
**Settings → Pages → Source = Deploy from a branch → main / root**. Wait ~1 min
for the first build. Privacy URL =
`https://<user>.github.io/<app>-landing/privacy.html`.

### Path B — Firebase Hosting (only if Firebase already configured)

Needs the Firebase CLI. Have the developer run `npm i -g firebase-tools` then
`firebase login` (opens a browser, once). Then from the project root:

1. `firebase init hosting` — pick the **existing** Firebase project, set **public
   directory** to `landing`, configure as single-page app: **No**, and do **not**
   overwrite existing files.
2. `firebase deploy --only hosting`.

Privacy URL = `https://<project-id>.web.app/privacy.html`.

### Path C — Vercel (guided)

`npm i -g vercel`, then `vercel login` (browser, once), then
`vercel deploy landing --prod`. Privacy URL = `https://<project>.vercel.app/privacy.html`.
Custom domain later via the Vercel dashboard → Domains.

### Path D — Self-host

Upload the **contents of `landing/`** to their host / domain root (static bucket,
FTP, whatever). Privacy URL = `https://<their-domain>/privacy.html`.

### After hosting (every path)

Show this verbatim:

> Once it's live:
> 1. Copy your **privacy URL** (`…/privacy.html`).
> 2. Play Console → **App content → Privacy policy** → paste it.
> 3. Set `KitConfig.PRIVACY_URL` = `…/privacy.html` and `KitConfig.TERMS_URL` =
>    `…/terms.html` so Settings → Privacy / Terms open the live pages in-app.

For a one-command backup of the whole project (app + `landing/`) to your own
GitHub, see **`/kit-save-to-github`**.
