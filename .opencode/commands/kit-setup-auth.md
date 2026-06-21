---
description: Choose and configure the kit's authentication provider
---

You are running **`/kit-setup-auth`** for NowKit. Goal: pick and wire the
authentication provider.

Audience: first-time mobile developers. Be brief; you make the edits, they answer
questions and supply credentials.

**Docs:** full walkthrough with screenshots — https://kit.shipkaro.dev/docs/auth

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase, reorder, or improvise the steps. The
prose outside those blocks is instructions for you, not for the developer.

## Step 0 — Detect existing state

Before walking the full setup, check what's already configured:

1. Read `KitConfig.kt` — note `AUTH_ENABLED`, `AUTH_PROVIDER`,
   `EMAIL_SIGN_IN_ENABLED`, `GOOGLE_SIGN_IN_ENABLED`, `GOOGLE_WEB_CLIENT_ID`.
2. Read `local.properties` (if it exists) — check `supabase.url` and
   `supabase.key` are set + non-blank (only relevant if `AUTH_PROVIDER` is
   SUPABASE).
3. Check whether `app/google-services.json` exists (relevant if `AUTH_PROVIDER`
   is FIREBASE).

Branch:

- **Auth is off** (`AUTH_ENABLED = false`) — the dev probably hasn't run this
  command yet. Walk the full flow.
- **Auth is fully configured** for the selected provider — ask the user (wait for their answer):
  - **Keep as-is** (recommended) — exit without changes.
  - **Switch provider** — walk the full flow (overwrites `AUTH_PROVIDER` +
    credentials).
  - **Toggle sign-in methods** — jump to Step 3 (Email / Google / Both picker).
  - **Update Google Web Client ID only** — jump to sub-step 4a.4 (Supabase) or
    4b.5 (Firebase) for re-paste.
  - **Re-register SHA-1 only** — jump to sub-step 4a.7 (Supabase) or 4b.3
    (Firebase). Useful after a debug-keystore reset.
- **Provider chosen but credentials missing** (e.g. `AUTH_PROVIDER = SUPABASE`
  but `supabase.key` blank) — tell the developer which pieces are missing and
  walk only those sub-steps.

## Step 1 — Choose a provider

First ask the user (wait for their answer) whether the app needs auth at all. If they pick "no
auth", set `AUTH_ENABLED = false` in `KitConfig` and stop here.

Otherwise ask the user (wait for their answer) which provider — present these two options:

- **Supabase (recommended)** — real email/password + Google sign-in, backed
  by a Supabase project. Needs a Supabase project URL and anon key. Smoother
  dev experience (no Gradle plugins to wire).
- **Firebase** — Firebase Authentication. Needs a `google-services.json`
  file and the Google Services Gradle plugin (the command sets this up).

## Step 2 — Set the provider in KitConfig

Find `KitConfig.kt` (search under `app/src/main/java`, package `...core.config`).
Set both:

    const val AUTH_ENABLED: Boolean = true
    val AUTH_PROVIDER: AuthProvider = AuthProvider.<SUPABASE|FIREBASE>

`AUTH_ENABLED` ships `false` by default so a fresh clone runs without an auth
screen — flipping it to `true` here turns the kit's auth flow back on for
this app.

## Step 3 — Choose sign-in methods

Ask the user (wait for their answer) which sign-in methods the auth screen should offer:

- **Email + Google (recommended)** — both the email/password form and the
  "Continue with Google" button.
- **Email only** — email/password form, no Google button.
- **Google only** — only the "Continue with Google" button, no email form.

Set the two flags in `KitConfig.kt` to match the answer:

    const val EMAIL_SIGN_IN_ENABLED: Boolean = <true|false>
    const val GOOGLE_SIGN_IN_ENABLED: Boolean = <true|false>

- Email + Google → both `true`
- Email only → `EMAIL_SIGN_IN_ENABLED = true`, `GOOGLE_SIGN_IN_ENABLED = false`
- Google only → `EMAIL_SIGN_IN_ENABLED = false`, `GOOGLE_SIGN_IN_ENABLED = true`

Remember whether Google is enabled — **if it is NOT, skip every Google-related
instruction in the steps below.**

## Step 4a — Supabase credentials

**Pacing rule for this step (important — read before you start):** the
developer is a non-coder switching between this terminal, a browser, and
sometimes their email inbox. Show **one sub-step at a time**, then **stop and
wait** for them to say "done" / "next" / "ready" before printing the next
sub-step. Do not dump the whole Supabase set in one message — they lose place.
Do not run `./gradlew signingReport` (or any other command) until the prior
sub-step is explicitly confirmed.

### Sub-step 4a.1 — Create the project

Show this verbatim. Then **STOP and wait** for "done":

> **Create your Supabase project**
> 1. Go to https://supabase.com and sign in.
> 2. Click **New Project**, give it a name, pick a region, set a database
>    password, click **Create new project**.
> 3. Wait for the project to finish provisioning (~1 minute) before continuing.
>
> Say "done" when the project dashboard is open.

### Sub-step 4a.2 — Grab the Project ID + anon key

Show this verbatim. Then **STOP and wait** for "done":

> **Get your Supabase keys**
> 1. Open **Project Settings → General** (gear icon, left sidebar). Copy
>    the **Project ID** (your URL becomes `https://<Project-ID>.supabase.co`).
> 2. Open **Project Settings → API Keys**, then click the **Legacy anon,
>    service_role API keys** tab.
> 3. Copy the **anon public** key.
>
> Paste both values back here when ready and say "done".

When the developer pastes them, write `local.properties` (git-ignored). Build
the URL from the Project ID:

    supabase.url=https://YOUR-PROJECT-ID.supabase.co
    supabase.key=YOUR-ANON-PUBLIC-KEY

Confirm what you wrote, then continue.

**If Google sign-in is NOT enabled, skip the rest of Step 4a and go to Step 5.**

Otherwise walk the Google sub-steps below **one at a time** — don't dump the
whole flow on the developer up front (it's overwhelming and they don't need the
map, just the next step). For your own context: Google sign-in needs a **Web
OAuth client** (pasted into Supabase) and an **Android OAuth client** (with the
debug SHA-1); the sub-steps cover both in order. Just start with 4a.3.

### Sub-step 4a.3 — Create the OAuth consent screen

Show this verbatim. Then **STOP and wait** for "done":

> **Set up the OAuth consent screen** (one-time per Google Cloud project).
> Google recently renamed this to **Google Auth Platform** — same thing, new wizard.
> 1. Open https://console.cloud.google.com — sign in with the same Google
>    account that owns the Supabase project (easier later) or a separate
>    Google Cloud account, either works.
> 2. Top bar → project picker → **New Project** → name it (e.g. "NowKit
>    auth") → **Create**. Wait for it to finish, then make sure the new
>    project is selected in the top bar.
> 3. Left sidebar → **APIs & Services → OAuth consent screen** (it opens the
>    **Google Auth Platform** page). If it says "Google Auth Platform not
>    configured yet", click **Get started**.
> 4. **App Information** → App name (your app's display name) + User support
>    email → **Next**.
> 5. **Audience** → pick **External** → **Next**. (This is the old "External"
>    choice — it just lives inside the wizard now.)
> 6. **Contact Information** → your email → **Next**.
> 7. Agree to the policy → **Create**.
>
> Say "done" when the **Audience** page shows **Publishing status: Testing**.

### Sub-step 4a.4 — Create the Web OAuth client

Show this verbatim. Then **STOP and wait** for the Client ID + Secret:

> **Web OAuth client** — what Supabase will use to talk to Google
> 1. Still in https://console.cloud.google.com (your new project).
> 2. Left sidebar → **Clients → + Create client** (under **Google Auth Platform**; the old **APIs & Services → Credentials → + Create credentials → OAuth client ID** path still works too).
> 3. Application type: **Web application**.
> 4. Name: `Supabase Web` (anything — for your reference).
> 5. **Authorized redirect URIs → + Add URI** → paste this exactly:
>
>        https://YOUR-PROJECT-ID.supabase.co/auth/v1/callback
>
>    (replace `YOUR-PROJECT-ID` with the Project ID from sub-step 4a.2).
> 6. Click **Create**.
> 7. A modal appears with **Client ID** + **Client secret** — copy BOTH and
>    paste them back here.
>
> Don't close the modal until you have both values pasted.

Replace `YOUR-PROJECT-ID` with the actual Project ID in the printed text
before showing it to the developer.

When the developer pastes the Client ID + Secret, remember both. Edit
`KitConfig.kt` now:

    const val GOOGLE_WEB_CLIENT_ID: String = "<the Client ID just pasted>"

Confirm what you wrote.

Then call /kit-setup-auth-part-2 to continue.
