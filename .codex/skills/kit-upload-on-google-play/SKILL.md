---
name: kit-upload-on-google-play
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

## Resume check — do this FIRST

Releasing is a long flow with many prepared assets, often done over several
sessions. The assets live in the repo (durable across sessions), so before Step 1
scan for what already exists and skip finished work:

- **Keystore (first-version C):** `release.keystore` present, OR `local.properties`
  has `release.store.file`, OR `$RELEASE_STORE_FILE` set → signing is ready.
- **Screenshots (E):** PNGs in `playstore/screenshots/` → done.
- **Listing copy (F):** `playstore/title.txt` + `short_description.txt` +
  `full_description.txt` all exist → ASO done.
- **Legal (G):** `playstore/privacy_policy.html` + `play_data_safety.md` exist (and
  `KitConfig.PRIVACY_URL` set) → legal done; a `landing/` dir → landing page done.
- **Changelog (J / update D):** `playstore/changelogs/<versionCode>.txt` for the
  current `versionCode` exists → release notes done.

After the developer picks first-version vs update (Step 1), tell them what you're
skipping ("listing copy already in `playstore/` — skipping F") and walk only the
remaining steps. Steps that **can't** be detected from files (app icon, SHA-1
registration, Play Console app creation, analytics insertion, the manual upload)
are always offered — confirm them with the developer. Each inlined sub-command
(`/kit-generate-legal`, `/kit-generate-screenshots`, `/kit-generate-aso`) also
detects its own output, so finished assets aren't redone.

The two paths cover these steps. Walk only the steps for the path the developer
picks; skip a step if the Resume check above already found its output.

First-version path steps:

- A — Pre-flight check
- B — App icon
- C — Release keystore
- D — Register release SHA-1 (Google sign-in)
- E — Screenshots
- F — Store listing copy (ASO)
- G — App content: privacy, Data Safety + content declarations
- H — Plan release analytics
- I — Create Play Console app
- J — Set version, changelog, build signed AAB
- K — Manual upload

Update path steps:

- A — Pre-flight check
- B — Bump version
- C — Update RemoteAppConfig (if wired)
- D — Release notes
- E — Screenshots (if UI changed)
- F — Plan release analytics
- G — Build signed AAB
- H — Manual upload

## Step 1 — First version or update?

Ask the developer (wait for their answer):
- **First version (never published)** — set up signing, generate assets, create
  the Play Console app, build the first AAB, upload manually.
- **Update an existing app** — bump version, write release notes, build, and
  upload manually in Play Console.

Then branch to the matching path below. Skip the path the developer did not
pick.

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
> 1. **One-time products** created + activated in Play Console.
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

Show this verbatim. Ask the developer (wait for their answer):

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
upload). Tell them so, skip this step for now, and continue. They'll come
back to D.2A after K.

### D.2A — Play App Signing path (post-first-upload)

Show this verbatim. **STOP and wait** for the SHA-1 value:

> Get your **release SHA-1** from Play Console:
> 1. Left sidebar → **Protected with Play** → scroll to **Play Store protection**
>    → **Protect app signing key** row → click **Manage Play app signing**. (If
>    Google has moved it, the top **search bar** → `app signing` is nav-proof.)
> 2. On that page, under **App signing key certificate**, copy the **SHA-1** value.
> 3. Paste it back here.
>
> Don't see it? The page only appears once a build with **Play App Signing** is on
> a track — your internal-testing upload covers that, so give it a minute after
> the upload finishes, then try again.

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

## E. Screenshots

Call `/kit-generate-screenshots`. That command handles the
generate-via-skill OR drop-your-own branch, the Play-Store-correct
dimensions (1080×1920 phone), and the output folder
(`playstore/screenshots/`). Skip its Verify step here — Step J below builds.

Skip this step if the developer already has screenshots in
`playstore/screenshots/` from a previous release that they don't want to
redo.

## F. Store listing copy (ASO)

Call `/kit-generate-aso`. That command drives the `aso-googleplay-listing`
skill — it derives a keyword strategy, confirms which keyword groups to target,
and writes app name, short + long description tuned for Play SEO. The three
files end up at:

- App name → `playstore/title.txt`
- Short description → `playstore/short_description.txt`
- Long description → `playstore/full_description.txt`

Skip this step if the developer already has listing copy they're
happy with from a previous run.

## G. App content — privacy, Data Safety + content declarations

**G.1 — Legal.** Call `/kit-generate-legal`. That command scans the active
SDKs + KitConfig, asks the developer the legal questions (company name, contact
email, jurisdiction, GDPR / CCPA / COPPA scope), and writes:

- `playstore/privacy_policy.md` — source of truth.
- `playstore/privacy_policy.html` — the page Play scrapes (needs a public URL).
- `playstore/play_data_safety.md` — filled Play Console form answers.

**G.2 — Landing page (gives you the public privacy URL).** Play requires a
**public** privacy-policy URL — the `.html` has to be hosted somewhere. Offer to
generate a simple landing page that hosts it. Ask the developer (wait for their
answer):

- **Generate a landing page** (recommended) — call `/kit-generate-landing`.
  It builds a static `landing/` site (hero + features + screenshots +
  Play badge + **privacy** + **terms** + contact) reusing the privacy policy
  from G.1. The hosted `…/privacy.html` becomes the URL you paste into Play.
- **I'll just host the privacy file myself** — skip the landing page; host
  `playstore/privacy_policy.html` directly.

After the privacy page is hosted (via the landing page or directly), show:

> **Finish the legal setup in Play Console:**
> 1. Copy your public privacy URL (`…/privacy.html` if you made a landing page,
>    else wherever you hosted `privacy_policy.html`).
> 2. **App content → Privacy policy** → paste that URL.
> 3. **App content → Data safety** → open `playstore/play_data_safety.md`
>    side-by-side and fill the web form.
> 4. Update `KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` to the hosted
>    URLs so Settings → Privacy / Terms open them in-app.

Wait for the developer to confirm the policy is hosted and the Data Safety
form is submitted before continuing.

**G.3 — The rest of the "Set up your app" content declarations (codebase-aware).**
These are the **App content** declarations Play forces beyond Data Safety — content
rating, target audience, ads, financial features, health, government. They're what
trigger the "*You must complete…*" / "*You must let us know whether…*" errors that
block a release. Nothing auto-fills them, but you can **infer almost all from the
codebase** so the developer just confirms instead of guessing.

**First, inspect the app** (do this for them; don't ask what you can read):
- App identity → `app/build.gradle.kts` `namespace` + `applicationId`, app display
  name in `strings.xml`, and the `feature/` folder names → infer the app's category
  (productivity / health / finance / social / kids / …).
- **Ads** → grep deps for an ads SDK (`play-services-ads` / AdMob / AppLovin / any
  ad network) in `app/build.gradle.kts` + `gradle/libs.versions.toml`. None → **No
  ads** (the kit ships none).
- **Financial features** → grep for banking / crypto / lending / investing / money-
  transfer / insurance SDKs. None → **No**. ⚠️ Selling a subscription or IAP via
  RevenueCat / Play Billing is **NOT** a "financial feature" — don't flag it as one.
- **Health** → grep for Health Connect (`androidx.health`) / Google Fit / medical
  SDKs, and check whether the app's purpose is health/fitness (feature names,
  strings). None and not a health app → **No**.
- **Target audience / children** → check for any child-directed signal (a kids
  feature, COPPA flag). The kit isn't child-directed by default → **not designed for
  children**, target **13+**.
- **Government / news** → almost always **No** for a kit app.

**Then write `playstore/app_content_declarations.md`** — a cheat-sheet with the
**suggested answer + the evidence** for each declaration (e.g. "Ads: **No** — no ad
SDK in build.gradle"), so the developer fills the Play form fast and it survives
across sessions. Present the suggestions and let them **confirm or correct each one**
— these are legal-ish declarations, so they own the final answer.

Then show (substitute the inferred app category + suggested answers):

> **Complete Play Console → your app → "Set up your app" → these App content
> sections** (each is required; the release stays blocked until all are green). Use
> `playstore/app_content_declarations.md` as your answer key:
> 1. **Content rating** — start the IARC questionnaire, enter your email, pick the
>    category (e.g. *<inferred category>*). For a typical app, answer **No** to the
>    violence / sexual content / gambling / drug-reference questions; the rating is
>    auto-calculated. Submit.
> 2. **Target audience and content** — pick age groups. Suggested: **<13+ / not
>    designed for children>**. Saying it's for children triggers extra
>    Families/COPPA rules — only do that if it's truly a kids' app.
> 3. **Ads** — declare whether the app contains ads. Suggested: **<No>**.
> 4. **Financial features** — suggested: **<No>** (a paid subscription is *not* a
>    financial feature). Tick the categories that apply, or "My app doesn't provide
>    any financial features".
> 5. **Health** — suggested: **<No>**. If it *is* a health/fitness app, select the
>    relevant items and complete the **health declaration**.
> 6. **Government apps** (and **News**, if shown) — suggested: **<No>**.

Wait for the developer to confirm all App content sections are green before
continuing.

## H. Plan release analytics

Call `/kit-plan-release-analytics` (don't ask permission — funnels are
load-bearing for "did this release work?"). That command asks the developer
the release goal, suggests 3–5 events, and wires them into the codebase. Skip
its own Verify step here — Step J below builds.

If the developer types "skip" when asked the release goal, skip this step
and continue.

## I. Create the Play Console app

Manual web step. **First read the `applicationId`** from `app/build.gradle.kts`
(the `applicationId = "…"` line in `defaultConfig`) and substitute its real value
into the **Package name** line below — the developer won't remember it. Then
present verbatim:

> **Create your app in Play Console:**
> 1. Open https://play.google.com/console → **Create app**.
> 2. **App name** — your app's public name (max 30 chars; changeable later).
> 3. **Package name** — paste exactly: `<APPLICATION_ID>`
>    This is your app's package from `app/build.gradle.kts`. It must match your
>    build or Play rejects the upload, and it's **permanent** once the app is
>    created. Click **Check availability**.
> 4. **Default language** — your main locale (e.g. English (US)).
> 5. **App or game** — **App**.
> 6. **Free or paid** — **Free** if you sell subscriptions / in-app purchases (the
>    download is free; you charge via billing); **Paid** only if users pay upfront
>    to install. You can't switch **Free → Paid** after publishing.
> 7. Tick the declarations → **Create app**.
> 8. Complete the **Set up your app** checklist (target audience, ads, content
>    rating, government apps if relevant, news app if relevant, COVID-19
>    contact-tracing if relevant).

Wait for the developer to confirm the app exists in Play Console.

## J. Set version, write changelog, build the signed AAB

**J.1 — Bump the version (do this BEFORE building).** Every build you upload to
Play needs a **unique, higher `versionCode`** — Play rejects a duplicate, which is
the #1 reason "my upload won't go through". In `app/build.gradle.kts`
`defaultConfig`:

    versionCode = N
    versionName = "X.Y.Z"

- **Very first upload ever:** `versionCode = 1` is fine, leave it.
- **Any re-upload** (you already pushed a build to *any* track, including internal
  testing): **increment `versionCode` by 1** and ask the developer for the next
  `versionName` (semver — `0.1.1` patch, `0.2.0` minor).
- When unsure, bump — a higher versionCode is always safe; a duplicate is always
  rejected. Edit both lines and confirm what you wrote.

**J.2 — Generate the "What's new" changelog.** Call **`/kit-generate-changelog`**
— it writes `playstore/changelogs/<versionCode>.txt` from git history since
the last release. If the developer says skip, write a one-line file yourself
(`Initial release.` or `Bug fixes and improvements.`).

**J.3 — Build.** From the project root:

    ./gradlew bundleRelease

The signed Android App Bundle lands at:

    app/build/outputs/bundle/release/app-release.aab

If it fails, the usual cause is the signing env vars not being picked up — open
a new terminal and re-check `echo $RELEASE_STORE_FILE`.

## K. Upload — manual

Show this:

> **Upload your release manually in Play Console:**
> 1. Open your app → **Internal testing → Create new release** (recommend
>    internal testing first; you can promote to Production later from the
>    same screen).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Open `playstore/title.txt`, `playstore/short_description.txt`, and
>    `playstore/full_description.txt` and paste each into the matching Play
>    Console field (Main store listing).
> 4. Upload the PNGs from
>    `playstore/screenshots/` to the
>    **Phone screenshots** section.
> 5. Paste the release notes from `playstore/changelogs/<versionCode>.txt`
>    (from J.2) into the **Release notes** field.
> 6. **Save → Review release → Start rollout to Internal testing**.
>
> Once you've smoke-tested it, promote to Production from the same screen.

---

# Update path

## A. Pre-flight check

Confirm:
- The previous release was shipped from this machine (or the developer has the
  same `release.keystore` file from the first release).
- Signing env vars (`RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
  `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`) are still set. Run
  `echo $RELEASE_STORE_FILE` to verify.

Ask the developer (wait for their answer): "Did any SDKs change since the last
release — enabled / disabled auth, paywall, analytics, AI, new Retrofit
endpoints, new Supabase tables?" If **yes**, call `/kit-generate-legal`
to regenerate the privacy policy + Data Safety mapping; tell them to re-host
the new `playstore/privacy_policy.html`. If **no**, skip — the existing hosted
policy is still accurate.

## B. Bump the version

Open `app/build.gradle.kts`. In `defaultConfig` find:

    versionCode = N
    versionName = "X.Y.Z"

Ask the developer for the next `versionName` (semver — e.g. `0.2.0` for a minor
feature release, `0.1.1` for a patch). Increment `versionCode` by **1** from
its current value. Update both lines — this is **mandatory**: Play rejects any
upload whose `versionCode` matches one already uploaded to any track.

## C. Update RemoteAppConfig (if wired)

If `KitConfig.REMOTE_CONFIG_PROVIDER` is `FIREBASE` or `SUPABASE`, the kit reads
`latest_version` / `latest_version_name` / `app_changelog` from the backend.
Remind the developer to bump these on their backend **after** the Play release
goes live, so users see the new version in the in-app "What's New" sheet and
the soft-update gate triggers correctly.

Show this:

> **Backend keys to update after Play release goes live:**
> - `latest_version` — set to the new `versionCode`.
> - `latest_version_name` — set to the new `versionName`.
> - `app_changelog` — append a new entry (JSON) with your release notes.
>
> Keep `min_supported_version` as it was unless you want to force users on
> older builds to update.

## D. Release notes

Call **`/kit-generate-changelog`** — it writes
`playstore/changelogs/<versionCode>.txt` (`<versionCode>` = the new value from
step B) from git history since the last release, translated into user-facing
bullets (plain text, ≤ 500 chars — Play's limit). Review with the developer and
let them edit. If they'd rather write it by hand, take 3–5 short plain-text
bullets and write the same file yourself.

## E. Screenshots (if UI changed)

Ask the developer (wait for their answer):
- **UI changed — redo screenshots** — call `/kit-generate-screenshots`.
- **UI unchanged — skip** — existing screenshots in `playstore/screenshots/`
  stay.

## F. Plan release analytics

Call `/kit-plan-release-analytics`. It reads the release notes from
Step D + the current codebase, asks the developer the release goal, suggests
3–5 events, and wires them in. Skip its Verify step — Step G below builds.

If the developer types "skip" when asked the release goal, skip this step
and continue.

## G. Build the signed AAB

    ./gradlew bundleRelease

## H. Upload — manual

Show this:

> **Upload this update manually in Play Console:**
> 1. Open your app → **Internal testing → Create new release** (or whichever
>    track you ship from — Closed / Open testing / Production).
> 2. Drop in `app/build/outputs/bundle/release/app-release.aab`.
> 3. Paste the release notes from
>    `playstore/changelogs/<versionCode>.txt` into the
>    **Release notes** field.
> 4. If you re-did screenshots in step E, upload the new PNGs from
>    `playstore/screenshots/`.
> 5. **Save → Review release → Start rollout**.

---

## Wrap up

Summarise:
- Which path ran (first version / update).
- What was set up vs skipped (e.g. "screenshots generated", "data safety form
  pending in Play Console", "release notes file written").
- The single next action — usually one of:
  - "Smoke-test on Internal testing, then promote to Production in Play Console."
  - "Bump `latest_version` / `app_changelog` in your backend RemoteAppConfig now."
  - "Complete the Play Console **Set up your app** checklist before your
    release can roll out."

Keep it to a few lines.
