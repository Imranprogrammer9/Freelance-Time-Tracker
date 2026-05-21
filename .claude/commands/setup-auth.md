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

## Step 3a — Supabase

Show the developer exactly this:

> **Get your Supabase keys**
> 1. Go to https://supabase.com and sign in.
> 2. Click **New Project**, give it a name, pick a region, create it.
> 3. Open **Project Settings → API** (gear icon, left sidebar).
> 4. Copy **Project URL** — this becomes your `supabase.url`.
> 5. Copy the **anon public** key — this becomes your `supabase.key`.
>
> **Enable Google sign-in** (optional — skip if you only want email/password)
> 6. Go to **Authentication → Providers → Google** and toggle it on.
> 7. Copy the **Authorized Client ID** of type *Web application*.
>
> **Add the redirect URL**
> 8. Go to **Authentication → URL Configuration → Redirect URLs**.
> 9. Add this exact value: `shipkaro://auth-callback`

Then make these edits yourself:
- Write the URL + key into `local.properties` (git-ignored — never committed):

      supabase.url=https://YOUR-PROJECT.supabase.co
      supabase.key=YOUR-ANON-KEY

- If they enabled Google sign-in, set the client ID in `KitConfig.kt`:

      const val GOOGLE_WEB_CLIENT_ID: String = "..."

OAuth callback note: the kit uses `shipkaro://auth-callback`, defined in
`AppModules.kt` (the `authModule` `scheme`/`host`) and `AndroidManifest.xml`. The
redirect URL from step 9 must match it. If the developer wants a custom scheme
instead of `shipkaro`, edit BOTH files to match.

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/supabase

## Step 3b — Firebase

Firebase auth needs the Google Services plugin. Read
`.claude/commands/setup-firebase.md` and follow it to add `google-services.json`
and apply the plugin, then return here.

Show the developer exactly this:

> **Get your Google Web client ID**
> 1. Open https://console.firebase.google.com and select your project.
> 2. Go to **Project settings** (gear icon) → **General**.
> 3. Scroll to **Your apps** and select the Android app.
> 4. Copy the **Web client ID**. (Not shown? Open Google Cloud Console →
>    Credentials → the OAuth 2.0 Client of type *Web application*.)

Then set it in `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/firebase

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If they configured Supabase or Firebase, remind them the real test is signing in
on a device (covered by `/run-kit`). Report what was set, and that any secrets
live in `local.properties`, which is never committed.
