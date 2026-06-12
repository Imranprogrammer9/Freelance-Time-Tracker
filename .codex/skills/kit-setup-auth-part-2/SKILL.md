---
name: kit-setup-auth-part-2
description: Continuation of kit-setup-auth
---
This continues **`/kit-setup-auth`** from sub-step 4a.5.

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim**. Same pacing rule applies — one sub-step at a time, wait
for "done", do not run shell commands before explicit confirmation.

### Sub-step 4a.5 — Connect Supabase to that Web client

Show this verbatim. Then **STOP and wait** for "done":

> **Tell Supabase about Google**
> 1. Back in your Supabase project → **Authentication → Providers → Google**.
> 2. Toggle **Enable Sign in with Google** → on.
> 3. Paste the **Client ID** (from sub-step 4a.4) into the **Client ID (for
>    OAuth)** field.
> 4. Paste the **Client Secret** (from sub-step 4a.4) into the **Client Secret
>    (for OAuth)** field.
> 5. Click **Save**.
>
> Say "done" once Supabase confirms the provider is saved.

### Sub-step 4a.6 — Add the in-app redirect URL

Show this verbatim. Then **STOP and wait** for "done":

> **In-app redirect URL** (so Supabase knows to bounce sign-in back to the app)
> 1. In Supabase, open **Authentication → URL Configuration → Redirect URLs**.
> 2. **+ Add URL** → paste this **exact** value (no edits):
>
>        shipkaro://auth-callback
>
> 3. Save.
>
> Say "done" when the redirect URL is saved.

OAuth callback note (for you, not the developer): the kit uses
`shipkaro://auth-callback`, defined in `AppModules.kt` (the
`supabaseClientModule` `install(Auth) { scheme/host }` block) and
`AndroidManifest.xml`. If the developer asks to use a custom scheme, edit
BOTH files to match before this sub-step is "done".

### Sub-step 4a.7 — Grab the debug SHA-1

Tell the developer:

> Next we need your app's **debug SHA-1 fingerprint**. It's a unique string
> that identifies *this build of your app on this machine* — Google uses it
> to confirm sign-in requests are really coming from your app and not a
> copycat. I'll run a Gradle command to read it. Say "yes" when ready.

**STOP and wait for the developer to say "yes" / "ready" / "go".** Do not run
the command before then. When confirmed, run:

    ./gradlew signingReport 2>&1 | grep "SHA-1\|SHA1" | head -1

(or the equivalent for their shell). Extract the SHA-1 line printed under the
`debug` variant and show it to the developer. Confirm they have it copied.

### Sub-step 4a.8 — Create the Android OAuth client

Show this verbatim. Then **STOP and wait** for "done":

> **Android OAuth client** — what your phone/emulator uses for native sign-in
> 1. Back in https://console.cloud.google.com (the same project as 4a.4).
> 2. **APIs & Services → Credentials → + Create credentials → OAuth client ID**.
> 3. Application type: **Android**.
> 4. Name: `Android (debug)`.
> 5. Package name: paste your `applicationId` (e.g. `dev.shipkaro.kit` if you
>    haven't renamed; otherwise the new ID from `/kit-change-app-id`).
> 6. **SHA-1 certificate fingerprint**: paste the SHA-1 from sub-step 4a.7.
> 7. Click **Create**.
>
> Say "done" once the Android client is saved. You don't need to copy any
> values from this one — Google links it to your app via the SHA-1.

Before a Play release the app also needs a **second** Android OAuth client
with the **release** SHA-1 added the same way — from your release keystore,
or Play Console → Test and release → App integrity → App signing if you use
Play App Signing. Mention this as a release-time task only — do NOT walk
through it now.

More detail + screenshots: https://kit.shipkaro.dev/docs/auth/supabase

## Step 4b — Firebase credentials

**Same pacing rule as 4a:** show one sub-step at a time, wait for "done"
between each, do not run shell commands before explicit confirmation.

### Sub-step 4b.1 — Wire google-services.json + plugin

Firebase auth needs the Google Services plugin + the `google-services.json`
file at `app/`. Call /kit-setup-firebase and follow it
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
