---
description: Choose and configure the kit's authentication provider
---

You are running **`/kit-setup-auth`** for ShipKit. Goal: pick and wire the
authentication provider.

Audience: first-time mobile developers. Be brief; you make the edits, they answer
questions and supply credentials.

**Docs:** full walkthrough with screenshots — https://kit.shipkaro.dev/docs/auth

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase, reorder, or improvise the steps. The
prose outside those blocks is instructions for you, not for the developer.

## Step 1 — Choose a provider

First ask (AskUserQuestion) whether the app needs auth at all. If they pick "no
auth", set `AUTH_ENABLED = false` in `KitConfig` and stop here.

Otherwise ask (AskUserQuestion) which provider — present these two options:

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

Ask (AskUserQuestion) which sign-in methods the auth screen should offer:

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

### Sub-step 4a.3 — Enable the Google provider on Supabase

Show this verbatim. Then **STOP and wait** for "done":

> **Enable Google on Supabase**
> 1. In your Supabase project, go to **Authentication → Providers → Google**.
> 2. Toggle it **on**.
> 3. Copy the **Authorized Client ID** field of type *Web application* (you'll
>    paste it here in the next sub-step — keep it open).
>
> Say "done" once Google is toggled on.

### Sub-step 4a.4 — Add the redirect URL

Show this verbatim. Then **STOP and wait** for "done":

> **Add the redirect URL**
> 1. In Supabase, open **Authentication → URL Configuration → Redirect URLs**.
> 2. Add this **exact** value (copy-paste, no edits): `shipkaro://auth-callback`
> 3. Save.
>
> Say "done" when the redirect URL is saved.

OAuth callback note (for you, not the developer): the kit uses
`shipkaro://auth-callback`, defined in `AppModules.kt` (the
`supabaseClientModule` `install(Auth) { scheme/host }` block) and
`AndroidManifest.xml`. If the developer asks to use a custom scheme, edit
BOTH files to match before this sub-step is "done".

### Sub-step 4a.5 — Save the Web client ID

Ask the developer to paste the **Web Client ID** they copied in 4a.3. When
they paste it, edit `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

Confirm what you wrote. Then continue.

### Sub-step 4a.6 — Grab the debug SHA-1

Tell the developer:

> Next we need your app's **debug SHA-1 fingerprint** so Google trusts this
> build. I'll run a Gradle command to read it — say "yes" when you're ready.

**STOP and wait for the developer to say "yes" / "ready" / "go".** Do not run
the command before then. When confirmed, run:

    ./gradlew signingReport 2>&1 | grep "SHA-1\|SHA1" | head -1

(or the equivalent for their shell). Extract the SHA-1 line printed under the
`debug` variant and show it to the developer. Confirm they have it copied.

### Sub-step 4a.7 — Register the SHA-1 in Google Cloud Console

Show this verbatim. Then **STOP and wait** for "done":

> **Register your app's SHA-1**
> 1. Open https://console.cloud.google.com → **APIs & Services → Credentials**
>    (pick the same Google Cloud project that owns the Web client ID from 4a.3).
> 2. Click **Create credentials → OAuth client ID → Android** (or open an
>    existing Android client).
> 3. Set the **Package name** to your `applicationId`.
> 4. Paste the **debug SHA-1** from 4a.6 into the **SHA-1 certificate
>    fingerprint** field.
> 5. Save.
>
> Say "done" once the Android OAuth client is saved.

Before a Play release the app also needs the **release** SHA-1 added the same
way — from your release keystore, or Play Console → Test and release → App
integrity → App signing if you use Play App Signing. Mention this as a
release-time task only — do NOT walk through it now.

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/supabase

## Step 4b — Firebase credentials

**Same pacing rule as 4a:** show one sub-step at a time, wait for "done"
between each, do not run shell commands before explicit confirmation.

### Sub-step 4b.1 — Wire google-services.json + plugin

Firebase auth needs the Google Services plugin + the `google-services.json`
file at `app/`. Read `.claude/commands/kit-setup-firebase.md` and follow it
inline. Skip its build/verify tail — this orchestrator builds at the end.

When `kit-setup-firebase` reports done, say so + **wait** for the developer
to say "next" before continuing.

**If Google sign-in is NOT enabled, skip the rest of Step 4b and go to Step 5.**

### Sub-step 4b.2 — Grab the debug SHA-1

Tell the developer:

> Next we need your app's **debug SHA-1 fingerprint** so Firebase trusts this
> build. I'll run a Gradle command to read it — say "yes" when ready.

**STOP and wait for "yes" / "ready" / "go".** Then run:

    ./gradlew signingReport 2>&1 | grep "SHA-1\|SHA1" | head -1

Show the SHA-1 from the `debug` variant. Confirm they have it copied.

### Sub-step 4b.3 — Register the SHA-1 in Firebase

Show this verbatim. Then **STOP and wait** for "done":

> **Add your SHA-1 to Firebase**
> 1. Open https://console.firebase.google.com → your project →
>    **Project settings** (gear icon) → **General**.
> 2. Under **Your apps**, click your Android app → **Add fingerprint**.
> 3. Paste the **debug SHA-1** from the previous sub-step. Save.
>
> Say "done" once the fingerprint is added.

### Sub-step 4b.4 — Re-download google-services.json

Show this verbatim. Then **STOP and wait** for the file path:

> **Re-download google-services.json**
> 1. Still on the Firebase **General** page, scroll to your Android app.
> 2. Click **Download google-services.json** — the new file includes the
>    OAuth client now.
> 3. Tell me the path where you saved it (e.g. `~/Downloads/google-services.json`).

When the developer pastes the path, copy the file to `app/google-services.json`
(overwrite the existing one).

### Sub-step 4b.5 — Save the Web client ID

Show this verbatim. Then **STOP and wait** for the value:

> **Copy your Web client ID**
> 1. Same Firebase **General** page — look under your Android app for the
>    **Web client ID** (auto-created with Google sign-in).
> 2. Don't see it? Open https://console.cloud.google.com → **APIs & Services
>    → Credentials** in the same project → look for the OAuth 2.0 Client of
>    type *Web application* → copy its Client ID.
> 3. Paste it back here.

When the developer pastes it, edit `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

Confirm what you wrote.

Before a Play release the app also needs the **release** SHA-1 added the same
way — from your release keystore, or Play Console → Test and release → App
integrity → App signing if you use Play App Signing. Mention this as a
release-time task only — do NOT walk through it now.

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/firebase

## Step 5 — Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If they configured Supabase or Firebase, remind them the real test is signing in
on a device (covered by `/kit-run-app`). Report what was set, and that any secrets
live in `local.properties`, which is never committed.
