---
description: Prepare release assets and upload your app to the Google Play Store
---

You are running **`/kit-upload-on-google-play`** for NowKit.

Goal: take a built app to a Google Play release — assets, signing, listing copy,
data-safety, AAB build, and upload. Two paths: **first version** (more setup) vs
**update** (just bump and ship).

Audience: first-time mobile developers / vibe coders. Several steps below
involve the Google Play Console web UI which cannot be automated — for those,
present the verbatim block clearly and wait for the developer to confirm before
continuing.

**Docs:** https://kit.shipkaro.dev/docs/release

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you.

## Step 1 — First version or update?

Ask the user (wait for their answer):
- **First version (never published)** — set up signing, generate assets, create
  the Play Console app, build the first AAB, upload manually.
- **Update an existing app** — bump version, write release notes, build, and
  upload manually in Play Console.

Then branch to the matching path below. Skip the path the developer did not
pick.

If the developer picks **First version**, continue with the First-version path
in this file, then call /kit-upload-on-google-play-part-2 for steps E–K.

If the developer picks **Update**, skip ahead — the Update path lives in
/kit-upload-on-google-play-part-3.

---

# First-version path

## A. Pre-flight check

Confirm before continuing:
- The app runs cleanly via `/kit-run-app` on a device.
- A Google Play Console account exists (https://play.google.com/console — one
  time $25 fee). If not, tell them to create it now and wait.
- Privacy + Terms URLs are settled. Don't validate them here — Step G runs
  `/kit-generate-legal` which produces both and walks the developer through
  hosting them.

### A.1 — Paywall billing readiness (only if the app sells)

Read `KitConfig.kt`. **If `PAYWALL_ENABLED = false`, skip this entirely** — the
app is free, no billing to wire.

If `PAYWALL_ENABLED = true`, the release-time RevenueCat + Play billing setup must
be done or real purchases won't credit users. First, **verify the billing
permission is declared** — check `app/src/main/AndroidManifest.xml` contains an
uncommented `<uses-permission android:name="com.android.vending.BILLING" />`. If
it's still commented (e.g. paywall enabled by hand), uncomment it now — Play
Console won't unlock product creation without it in the uploaded build.

The rest is dashboard-side, so confirm it with the developer. Show verbatim:

> **You have a paywall — is billing wired up?** Before real users can pay, you need:
> 1. **In-app products** created + activated in Play Console.
> 2. **A service-account JSON** uploaded to RevenueCat (~36 h to propagate).
> 3. **An offering + published paywall** in RevenueCat.
> 4. **This build live on a testing track + the tester opt-in URL opened** on your
>    device — Google Play only serves products to a build that's on a track, to
>    opted-in testers. (The internal-testing upload in the steps below covers this;
>    just remember to open the opt-in URL afterwards, or products stay empty.)
>
> Done already? Great — continue. Not yet? Full guide:
> **https://kit.shipkaro.dev/docs/paywall** (or `/kit-setup-paywall` → "Set up
> products + Play billing").

If they started this early (during `/kit-start-setup` Step 8), most of it should be
done — have them confirm the RevenueCat → Play connection shows **Verified** (the
~36 h propagation is complete) and an offering is published. If they haven't done it,
recommend they finish billing on the internal test track before promoting to
production. Don't block them — just make sure the choice is informed.

## B. App icon

A real launcher icon is required for a Play release. Show this:

> **Make your app icon:**
> 1. Go to https://icon.kitchen
> 2. Upload your logo (or pick an emoji / text) and set the background color.
> 3. Click **Download** — you get a ZIP containing `res/mipmap-*` folders.
>
> Once downloaded, **send me the path to the ZIP** and I'll copy the icons
> in for you.

Wait for the path. Unzip it into `app/src/main/res/`, overwriting the existing
`mipmap-*` folders, then confirm.

## C. Release keystore

The signing key is permanent — every release **must** be signed with the same
key. Losing it means you cannot update the app ever. Show this:

> **Generate your release keystore** (run in your project root):
>
>     keytool -genkey -v \
>       -keystore release.keystore \
>       -alias upload \
>       -keyalg RSA -keysize 2048 -validity 10000
>
> Answer the prompts (name, organisation, …). Choose **strong** passwords for
> the keystore and the key — write them down somewhere safe **right now**.
> Losing these = you cannot update the app, ever.
>
> Then **move `release.keystore` somewhere outside the repo** (e.g. `~/keys/`).

After they confirm, show this:

> **Set the signing env vars** — add to `~/.zshrc` (macOS / Linux) or
> equivalent on Windows:
>
>     export RELEASE_STORE_FILE=/absolute/path/to/release.keystore
>     export RELEASE_STORE_PASSWORD=<keystore-password>
>     export RELEASE_KEY_ALIAS=upload
>     export RELEASE_KEY_PASSWORD=<key-password>
>
> Open a **new terminal** so the vars take effect.

`app/build.gradle.kts` already reads these env vars — when they are present, the
release buildType is signed automatically.

## D. Register your release SHA-1 (Google sign-in)

**Detect first — do NOT show this section if Google sign-in is off.**

Read `KitConfig.kt`. If `AUTH_ENABLED = false`, OR `GOOGLE_SIGN_IN_ENABLED = false`,
OR `AUTH_PROVIDER = STUB`, skip this step and continue to E.

Otherwise: native Google sign-in needs the **release** SHA-1 registered the same
way the debug SHA-1 was during `/kit-setup-auth`. Without it, sign-in works on
your dev machine but fails for users on the Play build.

**Pacing rule** — same as `/kit-setup-auth`: one sub-step at a time, wait for
the developer to say "done" or "next" between sub-steps, do NOT run shell
commands without an explicit "yes".

### D.1 — Pick which release SHA-1 to register

Show this verbatim. Ask the user (wait for their answer):

> Google Play offers two ways to sign your release:
>
> - **Play App Signing (recommended)** — Google manages your release key.
>   You upload an AAB signed with your upload key; Google re-signs it with
>   their key for delivery. The SHA-1 on devices is Google's, so that's the
>   one you register.
> - **Self-managed keystore** — your `release.keystore` from Step C signs
>   the artifact that lands on devices. Register that keystore's SHA-1.
>
> If you don't know which, pick **Play App Signing** — it's the default
> Play flow and protects you against keystore loss.

If they pick **Play App Signing** and this is the very first upload of the
app, the signing key doesn't exist yet (Play creates it on first AAB
upload). Tell them so + note this step is skipped (do after first upload)
and continue. They'll come back to D.2A after K.

### D.2A — Play App Signing path (post-first-upload)

Show this verbatim. **STOP and wait** for the SHA-1 value:

> Get your **release SHA-1** from Play Console:
> 1. **Fastest:** use the Play Console **search bar** (top of the page) → type
>    `app signing` → open the result. (Google moves this page around; search is
>    nav-proof.) Otherwise navigate: left sidebar → expand **Test and release** →
>    **App integrity** → **App signing** tab.
> 2. Under **App signing key certificate**, copy the **SHA-1** value.
> 3. Paste it back here.
>
> Don't see it? The page only appears once a build with **Play App Signing** is on
> a track — your internal-testing upload covers that, so give it a minute after
> the upload finishes, then search again.

Continue to D.3.

### D.2B — Self-managed keystore path

Tell the developer:

> I'll read the SHA-1 from your `release.keystore` using `keytool`. Say
> "yes" when you're ready.

**STOP and wait** for "yes" / "ready". When confirmed, run:

    keytool -list -v \
        -keystore "$RELEASE_STORE_FILE" \
        -alias "$RELEASE_KEY_ALIAS" \
        -storepass "$RELEASE_STORE_PASSWORD" 2>&1 | grep "SHA1:"

(If the env vars are not set in this shell, fall back to asking the developer
for the keystore path / alias / password and substitute literal values.)

Show the SHA-1 line. Confirm the developer has it.

### D.3 — Register the SHA-1

Branch on `KitConfig.AUTH_PROVIDER`:

**If `SUPABASE`** — show this verbatim. **STOP and wait** for "done":

> Add your **release** SHA-1 to Google Cloud Console:
> 1. Open https://console.cloud.google.com — same project as the Web /
>    Android OAuth clients you created during `/kit-setup-auth`.
> 2. **APIs & Services → Credentials → + Create credentials → OAuth client
>    ID**.
> 3. Application type: **Android**.
> 4. Name: `Android (release)`.
> 5. Package name: your `applicationId`.
> 6. **SHA-1 certificate fingerprint**: paste the release SHA-1 from D.2.
> 7. Click **Create**.
>
> Don't delete the existing `Android (debug)` client — keep both. Debug
> sign-in works during development, release sign-in works for Play users.
>
> Say "done" once saved.

**If `FIREBASE`** — show this verbatim. **STOP and wait** for the file path:

> Add your **release** SHA-1 to Firebase:
> 1. Open https://console.firebase.google.com → your project → **Project
>    settings** (gear) → **General**.
> 2. Under **Your apps**, select your Android app → **Add fingerprint** →
>    paste the release SHA-1 from D.2. Save.
> 3. Click **Download google-services.json** — the new file now lists both
>    debug + release SHA-1s.
> 4. Tell me the path where you saved it.

When the developer pastes the path, copy the file to `app/google-services.json`
(overwrite existing). Then say:

> Done — the next `bundleRelease` build picks up the new file.

Then call /kit-upload-on-google-play-part-2 to continue with steps E–K.
