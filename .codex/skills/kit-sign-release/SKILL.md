---
name: kit-sign-release
description: Create a release keystore if missing, build a signed AAB, and guide uploading it to a Google Play testing track
---

You are running **`/kit-sign-release`** for NowKit.

Goal: get a **signed release AAB** built and onto a Google Play testing track, with
the **least friction**. This is the build that:
- registers your app's package on Play, and
- lets RevenueCat ↔ Google Play verify the connection (you do **not** need to
  create in-app products for that — products come later).

Audience: first-time mobile developers / vibe coders. Do the mechanical work
**for** them — generate the keystore, write the creds, run the build. Only the
Play Console web upload is manual (it can't be automated); present those blocks
verbatim and wait.

**Docs:** https://kit.shipkaro.dev/docs/release

When a block is quoted with `>`, present it to the developer **verbatim**. Prose
outside those blocks is instructions for you.

## Why signing first (the chicken-and-egg)

Play Console blocks a lot until a **signed build with the right permissions is on
a track**: product creation needs the `BILLING` permission in an uploaded build,
and RevenueCat can only verify the Play connection once the package is live on a
track. So the order is: **keystore → signed AAB → upload to a testing track**.
Products, store listing, and screenshots are a separate later step
(`/kit-upload-on-google-play`).

## Progress tracking

Call **TaskCreate** with these tasks, mark each `in_progress` on entry and
`completed` when done (prefix `[skipped] ` via **TaskUpdate** if skipped):

- 1 — Detect existing signing
- 2 — Create release keystore (if missing)
- 3 — Build signed AAB
- 4 — Create Play Console app (manual)
- 5 — Upload to a testing track (manual)

## Step 1 — Detect existing signing

The kit's `app/build.gradle.kts` signs the release build when it finds signing
config in **either** `RELEASE_*` env vars (CI) **or** `release.*` keys in
`local.properties` (local dev). Check both:

```bash
echo "env: ${RELEASE_STORE_FILE:-<unset>}"
grep -E '^release\.(store|key)\.' local.properties 2>/dev/null || echo "no release.* keys in local.properties"
```

Then resolve the keystore path it points to (env var wins, else the
`release.store.file` value) and check the file exists.

- **Signing already configured + keystore file exists** → say so, mark Step 2
  `[skipped]`, go to Step 3.
- **Otherwise** → go to Step 2.

> ⚠️ If you already shipped this app before, you **must** reuse the original
> keystore — a new key means Play rejects the upload. Find your old
> `release.keystore` and point `release.store.file` at it instead of making a
> new one.

## Step 2 — Create the release keystore

The signing key is **permanent**: every future update must be signed with the
same key. Lose it → you can never update the app again. Be explicit about this.

### 2.1 — Gather details (AskUserQuestion)

Ask the developer:
- **Passwords** — offer **"Generate strong ones for me (recommended)"** vs
  **"I'll provide my own"**. If generated, create them with
  `openssl rand -base64 18` (one for the store, one for the key) and you **must**
  show both to the developer afterwards with a hard instruction to save them in a
  password manager.
- **Org / name** for the certificate — fine to default. Use the app's display
  name for `CN` and a short org string; leave other `-dname` fields blank.

Key alias: default to `upload` (no need to ask).

### 2.2 — Generate the keystore

Generate it at the project root as `release.keystore` (already git-ignored by the
kit's `*.keystore` rule). Run non-interactively:

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass "<STORE_PASSWORD>" -keypass "<KEY_PASSWORD>" \
  -dname "CN=<app display name>, O=<org>, C=US"
```

### 2.3 — Persist the creds

Append to `local.properties` (git-ignored — same file the kit already uses for
Supabase / RevenueCat / PostHog keys). Use a **relative** path so it works from
any machine that has the repo + keystore:

```
release.store.file=release.keystore
release.store.password=<STORE_PASSWORD>
release.key.alias=upload
release.key.password=<KEY_PASSWORD>
```

Then show this verbatim, filling in the real passwords if you generated them:

> ✅ **Keystore created: `release.keystore`** (kept out of git).
>
> **Back these up right now — losing them means you can never update your app:**
> - Store password: `<STORE_PASSWORD>`
> - Key password: `<KEY_PASSWORD>`
> - Key alias: `upload`
>
> Save the passwords in a password manager, and copy `release.keystore` somewhere
> safe outside this folder (e.g. `~/keys/`). If you later move the keystore out
> of the repo, update `release.store.file` in `local.properties` to its new path.

## Step 3 — Build the signed AAB

Run from the project root:

```bash
./gradlew :app:bundleRelease --no-daemon
```

Output lands at:

```
app/build/outputs/bundle/release/app-release.aab
```

Verify it's actually signed (not the unsigned fallback):

```bash
$JAVA_HOME/bin/jarsigner -verify -verbose -certs \
  app/build/outputs/bundle/release/app-release.aab 2>&1 | grep -i "jar verified" \
  || echo "NOT signed — check release.* keys in local.properties"
```

If the build produced an **unsigned** bundle, the signing config wasn't picked
up — re-check Step 1's keys resolve to an existing file, then rebuild.

If `PAYWALL_ENABLED = true` in `KitConfig.kt`, confirm the build carries billing:
the manifest must have an uncommented
`<uses-permission android:name="com.android.vending.BILLING" />`. Without it, Play
won't unlock product creation later.

## Step 4 — Create the Play Console app (if not done)

Manual web step. **First read the `applicationId`** from `app/build.gradle.kts`
(the `applicationId = "…"` line in `defaultConfig`) and substitute its real value
into the **Package name** line below — the developer won't remember it. Then
present the block verbatim and wait:

> **Create your app in Play Console** (skip if it already exists):
> 1. Open https://play.google.com/console → **Create app** (pay the one-time $25
>    developer fee first if you haven't).
> 2. **App name** — your app's public name (max 30 chars; you can change it later).
> 3. **Package name** — paste exactly: `<APPLICATION_ID>`
>    This is your app's package from `app/build.gradle.kts`. It must match your
>    build exactly or Play rejects the upload, and it's **permanent** once the app
>    is created — don't typo it. Click **Check availability**.
> 4. **Default language** — your main locale (e.g. English (US)).
> 5. **App or game** — **App**.
> 6. **Free or paid** — pick **Free** if you sell subscriptions / in-app purchases
>    (the download is free; you charge through in-app billing). Pick **Paid** only
>    if users pay an upfront price to install. You **can't switch Free → Paid after
>    publishing**, so choose deliberately.
> 7. Tick the declarations → **Create app**.
>
> You don't need the whole "Set up your app" checklist yet — the app shell is
> enough to upload a testing build.

Wait for "done".

## Step 5 — Upload to a testing track

Manual web step — present verbatim and wait:

> **Upload your signed build to a testing track:**
> 1. In Play Console → your app → **Test and release → Testing → Closed testing**
>    (Internal testing is fine too and has no review wait).
> 2. **Create new release**.
> 3. If prompted about **Play App Signing**, accept it (recommended — Google
>    manages your distribution key; your `release.keystore` stays the upload key).
> 4. Upload `app/build/outputs/bundle/release/app-release.aab`.
> 5. Add a short release name + notes, **Save → Review release → Start rollout**.
>
> Once it's processed, your package is live on the track — Play Console stops
> asking for "a build with the BILLING permission", and RevenueCat can verify the
> connection.

## Wrap up

Summarise in a few lines:
- Keystore created (and the **back it up** reminder) or reused.
- AAB built + signed at `app/build/outputs/bundle/release/app-release.aab`.
- The single next action — usually: "Upload the AAB to Closed/Internal testing,
  then finish the RevenueCat ↔ Play connection (`/kit-setup-paywall`)."

For products, store listing, screenshots, and the full Production release, point
them to **`/kit-upload-on-google-play`**.
