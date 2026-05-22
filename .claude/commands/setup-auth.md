---
description: Choose and configure the kit's authentication provider
---

You are running **`/setup-auth`** for ShipKit. Goal: pick and wire the
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

Otherwise ask (AskUserQuestion) which provider — present these three options:

- **Stub (recommended to start)** — in-memory fake auth, no backend, no
  credentials. Sign-in always succeeds. Lets the app build and run instantly;
  switch to a real provider anytime by re-running this command.
- **Supabase** — real email/password + Google sign-in, backed by a Supabase
  project. Needs a Supabase project URL and anon key.
- **Firebase** — Firebase Authentication. Needs a `google-services.json` file.

## Step 2 — Set the provider in KitConfig

Find `KitConfig.kt` (search under `app/src/main/java`, package `...core.config`).
Set:

    val AUTH_PROVIDER: AuthProvider = AuthProvider.<STUB|SUPABASE|FIREBASE>

If they chose **Stub**: nothing else to do — confirm and stop.

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

Show the developer exactly this:

> **Get your Supabase keys**
> 1. Go to https://supabase.com and sign in.
> 2. Click **New Project**, give it a name, pick a region, create it.
> 3. Open **Project Settings → General** (gear icon, left sidebar). Copy the
>    **Project ID**. There is no "Project URL" field — your URL is
>    `https://<Project-ID>.supabase.co`.
> 4. Open **Project Settings → API Keys**, then click the **Legacy anon,
>    service_role API keys** tab.
> 5. Copy the **anon public** key — this becomes your `supabase.key`.

Write the URL + key into `local.properties` (git-ignored — never committed).
Build the URL from the Project ID:

    supabase.url=https://YOUR-PROJECT-ID.supabase.co
    supabase.key=YOUR-ANON-PUBLIC-KEY

**Only if Google sign-in is enabled**, also show the developer this:

> **Enable Google sign-in on Supabase**
> 6. Go to **Authentication → Providers → Google** and toggle it on.
> 7. Copy the **Authorized Client ID** of type *Web application*.
>
> **Add the redirect URL**
> 8. Go to **Authentication → URL Configuration → Redirect URLs**.
> 9. Add this exact value: `shipkaro://auth-callback`

Then set the client ID in `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

OAuth callback note: the kit uses `shipkaro://auth-callback`, defined in
`AppModules.kt` (the `authModule` `scheme`/`host`) and `AndroidManifest.xml`. The
redirect URL from step 9 must match it. If the developer wants a custom scheme
instead of `shipkaro`, edit BOTH files to match.

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/supabase

## Step 4b — Firebase credentials

Firebase auth needs the Google Services plugin. Read
`.claude/commands/setup-firebase.md` and follow it to add `google-services.json`
and apply the plugin, then return here.

**Only if Google sign-in is enabled**, show the developer this:

> **Get your Google Web client ID**
> 1. Open https://console.firebase.google.com and select your project.
> 2. Go to **Project settings** (gear icon) → **General**.
> 3. Scroll to **Your apps** and select the Android app.
> 4. Copy the **Web client ID**. (Not shown? Open Google Cloud Console →
>    Credentials → the OAuth 2.0 Client of type *Web application*.)

Then set it in `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/firebase

## Step 5 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If they configured Supabase or Firebase, remind them the real test is signing in
on a device (covered by `/run-kit`). Report what was set, and that any secrets
live in `local.properties`, which is never committed.
