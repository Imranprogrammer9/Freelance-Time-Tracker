---
description: Choose and configure the kit's authentication provider
---

You are running **`/setup-auth`** for ShipKit. Goal: pick and
wire the authentication provider.

Audience: first-time mobile developers. Be brief; you make the edits, they answer
questions and supply credentials.

## Step 1 — Choose a provider

First ask (AskUserQuestion) whether the app needs auth at all. If they pick "no
auth", set `AUTH_ENABLED = false` in `KitConfig` and stop here.

Otherwise ask (AskUserQuestion) which provider:

- **Stub (recommended to start)** — in-memory fake auth, no backend, no
  credentials. Sign-in always succeeds. Lets the app build and run instantly;
  they can switch to a real provider anytime by re-running this command.
- **Supabase** — real email/password + Google sign-in, backed by a Supabase
  project. Needs a Supabase project URL and anon key.
- **Firebase** — Firebase Authentication. Needs a `google-services.json` file.

## Step 2 — Set the provider in KitConfig

Find `KitConfig.kt` (search under `app/src/main/java`, package `...core.config`).
Set:

    val AUTH_PROVIDER: AuthProvider = AuthProvider.<STUB|SUPABASE|FIREBASE>

If they chose **Stub**: nothing else to do — confirm and stop.

## Step 3a — Supabase

Guide them: create a project at supabase.com, then Project Settings → API and
copy the **Project URL** and the **anon / public key**. Write these into
`local.properties` (git-ignored — never commit it):

    supabase.url=https://YOUR-PROJECT.supabase.co
    supabase.key=YOUR-ANON-KEY

For Google sign-in: in the Supabase dashboard, Authentication → Providers →
Google, enable it, and copy the **Authorized Client ID** (the "Web application"
type). Put it in `KitConfig.kt`:

    const val GOOGLE_WEB_CLIENT_ID: String = "..."

OAuth callback: the kit uses scheme `shipkaro` / host `auth-callback` — the
redirect URL is `shipkaro://auth-callback`. It is defined in two places:
`AppModules.kt` (the `authModule`, `scheme` / `host`) and `AndroidManifest.xml`
(an intent-filter). Tell the developer this redirect URL must also be added in
the Supabase dashboard under Authentication → URL Configuration → Redirect URLs.
Ask if they want to keep the kit's `shipkaro` scheme or use a custom one; if
custom, edit BOTH `AppModules.kt` and `AndroidManifest.xml` to match.

## Step 3b — Firebase

Firebase auth needs the Google Services plugin. Read
`.claude/commands/setup-firebase.md` and follow it to add `google-services.json`
and apply the plugin, then return here.

For Google sign-in, get the **Web client ID** from Firebase Console → Project
settings → your Android app, and set `GOOGLE_WEB_CLIENT_ID` in `KitConfig.kt` as
shown above.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

If they configured Supabase or Firebase, remind them the real test is signing in
on a device (covered by `/run-kit`). Report what was set, and that any secrets
live in `local.properties`, which is never committed.
