---
name: kit-generate-legal
description: Generate privacy policy + Play Data Safety from the actual codebase
---
You are running **`/kit-generate-legal`** for NowKit. Goal: scan what this app
*actually* does — which SDKs are wired, which `KitConfig` flags are on, which
custom endpoints send user data — and produce a draft privacy policy plus a
filled Play Data Safety form mapping.

Audience: first-time mobile developers / vibe coders. You make the edits.

This command differs from a hand-edited template: it reads the codebase, so the
output reflects only the data the app really collects. Re-run after toggling
SDKs or adding endpoints.

**Heads up — verbatim to the developer:**

> **This generates a draft, not legal advice.** The output is a starting point
> based on what the kit's automated scan can see. A real lawyer should review
> the final policy for your jurisdiction (especially if you target the EU
> under GDPR or California under CCPA). Saving you legal review by 80% is the
> goal — not eliminating it.

**Docs:** https://kit.shipkaro.dev/docs/legal

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim**. Prose outside those blocks is instructions for you.

**Never invent behaviour you didn't verify from the code.** Do NOT write claims like
"on-device", "processed locally", "audio is never stored", or "nothing is sent to us"
unless you confirmed it by reading the actual code. If the app has a voice / audio /
camera / upload feature and you cannot prove **from the code** that it stays on the
device, **ask the developer where the data goes** (Steps 2–3) and state only what they
confirm. A wrong "we don't collect / don't send X" claim is a Play policy violation and
legal exposure — far worse than omitting it. The kit ships **no** voice/audio feature by
default, so if you see one, it's custom — treat its data flow as unknown until confirmed.

## Step 0 — Detect existing legal docs

Check whether `playstore/privacy_policy.html` and `playstore/play_data_safety.md`
already exist.

- If both exist, this command was run before. Ask the user (wait for their answer)
  whether to keep them as-is (exit without changes) or regenerate (re-scan the
  codebase and overwrite — do this after toggling SDKs / adding endpoints).
- If neither (or only one) exists, continue to Step 1 and generate.

Do not silently overwrite existing legal docs.

## Step 1 — Scan SDKs + KitConfig

Read in parallel:

- `gradle/libs.versions.toml`
- `app/build.gradle.kts` (for `dependencies { ... }`)
- `app/src/main/java/dev/shipkaro/kit/core/config/KitConfig.kt`
- `app/src/main/java/dev/shipkaro/kit/core/data/settings/SettingsRepository.kt` (for the analytics opt-out toggle)

Build a table in memory of **active SDKs and what they collect**. Use this
reference (only include SDKs that are actually present **and** whose
`KitConfig` flag is enabled):

| SDK marker (dep / file) | Active when | Play data type | Purpose |
|---|---|---|---|
| `supabase-auth` | `AUTH_ENABLED && AUTH_PROVIDER=SUPABASE` | Email address; auth tokens | Account |
| `firebase-auth` | `AUTH_ENABLED && AUTH_PROVIDER=FIREBASE` | Email address; auth tokens | Account |
| Google Sign-In (`googleid` dep + `GOOGLE_SIGN_IN_ENABLED`) | `AUTH_ENABLED && GOOGLE_SIGN_IN_ENABLED` | Name, email, photo | Account |
| `revenuecat-purchases` | `PAYWALL_ENABLED` | Purchase history, anonymous user ID | Payments |
| `posthog-android` | `ANALYTICS_ENABLED && local.properties has posthog.api.key` | App events, device IDs, IP, approximate location (from IP) | Analytics |
| `firebase-analytics` | `ANALYTICS_ENABLED && google-services.json exists` | App events, device IDs, IP | Analytics |
| `firebase-crashlytics` | `ANALYTICS_ENABLED && google-services.json exists` | Crash logs, device info, OS, app build | Crash diagnostics |
| `firebase-messaging` | `google-services.json exists` AND grep finds `KitMessagingService` enabled in manifest | Push token | Push notifications |
| `firebase-config` | `REMOTE_CONFIG_PROVIDER=FIREBASE` | App-instance ID | Remote config |
| `supabase-postgrest` | `AUTH_PROVIDER=SUPABASE OR REMOTE_CONFIG_PROVIDER=SUPABASE` | Whatever the dev's tables hold (ask in Step 3) | App data |
| `core/ai/OpenRouter*` | `OPENROUTER_ENABLED` | Prompts sent to OpenRouter; routed to underlying providers (Anthropic, OpenAI, etc.) | AI features |

For Firebase pieces, check whether `app/google-services.json` exists; if it
doesn't, the SDKs are inert even though they're on the classpath — mark those
rows inactive.

For PostHog, read `local.properties` (if it exists) and check whether
`posthog.api.key` is non-empty.

## Step 2 — Scan for ALL outbound data flows (not just Retrofit)

Step 1 covers the kit's own SDKs. This step catches **anything the developer wired by hand** —
and a scan that only looks for Retrofit annotations will **miss raw OkHttp / WebSocket calls**,
which is exactly how transcription, AI, and upload code is usually written (e.g. streaming audio
to Deepgram over a WebSocket has no `@POST` marker at all). Grep the **whole** `app/src/main/java`
tree — including any new `core/` folders the developer added — for:

- **Retrofit interfaces:** `@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH`.
- **Raw HTTP / streaming:** `OkHttpClient`, `Request.Builder`, `newCall`, `newWebSocket`,
  `WebSocket`, `HttpURLConnection`, Ktor `HttpClient`, `callbackFlow` near a `socket`.
- **External hosts:** any `.url("http…")`, `"https://…"`, or hardcoded base-URL / hostname string.
- **Third-party keys / auth:** `Authorization`, `Bearer`, `apiKey`, `X-Api-Key`, and any
  `*_API_KEY` / `*_KEY` — plus any `local.properties` key that **isn't** a known kit one
  (supabase / revenuecat / posthog / openrouter).
- **Supabase tables:** `client.from("…")`.
- **Media / sensitive capture:** `MediaRecorder`, `AudioRecord`, `SpeechRecognizer`,
  `RECORD_AUDIO`, `CameraX` / `Camera`, `ACCESS_FINE_LOCATION` / location providers, file
  pickers / uploads.

**Collect the distinct external hosts** the app sends data to (e.g. `openrouter.ai`,
`api.deepgram.com`, `api.assemblyai.com`, `api.openai.com`). **Do NOT skip a host because it
looks kit-provided** — a hand-wired call to `openrouter.ai` *outside* `core/ai` still shares
user data and must be disclosed. If the scan finds **any** audio / camera / location / file
capture, treat that as a data flow to resolve here — never assume it stays on-device.

Show every finding back and **wait** for the answers:

> I scanned your code for outbound data. Here's every external service / endpoint your app sends
> data to, and any device capture I found:
>
> - <host / endpoint / capture> — <what the code appears to send>
>
> For **each one**, tell me what it actually sends: (a) **audio / voice**, (b) **images / video /
> files**, (c) **location**, (d) **free-form user text or content**, (e) **email / name / account
> info**, (f) **anonymous usage only**, or (g) **not user data — skip**. If a service transcribes,
> analyses, or stores anything, say so.

Record every answer — they feed Step 4 **and** the Data Safety form. **Any service you send audio
or user content to is a third-party data recipient and MUST be listed**, even if you added it by
hand and it isn't one of the kit's SDKs.

## Step 3 — Ask legal questions

Ask the user (wait for their answer) to collect the following. Ask each as a plain question — the
open-ended ones need a typed answer; the Yes/No ones can be a quick pick. Don't force a free-text
answer (name, email, region) into a fixed-option picker.

**Open-ended — ask as a plain question, wait for the typed answer:**
1. **Company / individual name** — appears as "we" in the policy.
2. **Contact email** — where users send privacy questions. Required.
3. **Jurisdiction** — country / state (affects GDPR / CCPA / LGPD / DPDPA callouts).
7. **Data retention window** — how long after account deletion data is fully purged (default 30 days).
8. **Server region** — where the backend hosts data (matters under GDPR / data-residency).
9. **Anything the scan couldn't see (ALWAYS ask this)** — present verbatim, wait for a typed answer:
   > Last check — since you cloned the kit, did you add **any** SDK, API, or service it didn't
   > ship? Transcription (Deepgram, AssemblyAI, Whisper), AI (OpenAI, your own OpenRouter calls),
   > maps, payments, file uploads, ads, chat — anything. And does your app send **audio, images,
   > video, files, or location** anywhere off the device? List whatever you added, or say "no".

   Fold every answer into the data table + Data Safety — a service that receives audio or user
   content is a third-party recipient you must disclose.

**Yes/No:**
4. **Target users in the EU / EEA?** → triggers GDPR section.
5. **Target users in California?** → triggers CCPA section.
6. **Target users under 13?** → triggers COPPA + Play family-policies warning.

## Step 4 — Generate Markdown

Write `playstore/privacy_policy.md`. Structure:

```markdown
# Privacy Policy — <App Name>

Last updated: <today's date>

<App Name> ("we", "us", "the app") respects your privacy. This document
explains what data the app collects, why, and your rights regarding it.

## 1. Data we collect

<Render a Markdown table from Step 1's active SDK list + Step 2's endpoint
answers. One row per data category. Columns: Category | Source | Why we
collect it.>

## 2. How we use your data

<Bullet list derived from each SDK's "Purpose" column.>

## 3. Sharing

We do not sell your personal data. The third-party services listed above
process data on our behalf:

<Bullet list of each active SDK with its provider URL — e.g.
"PostHog (PostHog Inc., https://posthog.com/privacy")>

## 4. Your rights

You can:
- Delete your account and all associated data in-app from Settings → Account
  → Delete account. <Only include if AUTH_ENABLED.>
- Opt out of analytics in Settings → Privacy → Analytics. <Only if
  ANALYTICS_ENABLED.>
- Request data export by emailing <contact email>.

Data is fully purged within <retention window> days of deletion.

## 5. Security

<Boilerplate: HTTPS in transit, AES-GCM at rest for any local secrets via
SecureDataStore, …>

## 6. Children

<Conditional on Q6.>

## 7. International transfers / GDPR

<Conditional on Q4.>

## 8. California residents / CCPA

<Conditional on Q5.>

## 9. Changes to this policy

We will update this page when our practices change. The "Last updated" date
at the top reflects the most recent change.

## 10. Contact

Privacy questions: **<contact email>**. <Company name>, <jurisdiction>.
```

Render the actual content — do not leave bracket placeholders.

## Step 5 — Generate HTML for hosting

Write `playstore/privacy_policy.html` — the same content rendered as a
self-contained HTML page. Inline a minimal `<style>` block (system font,
max-width 720px, line-height 1.6) so it renders cleanly when the developer
drops it on GitHub Pages / Netlify / Vercel / Cloudflare Pages.

Why both formats:
- `.md` is the source of truth — easy to diff and re-render.
- `.html` is what Google Play Console will scrape when the dev pastes the
  hosted URL. Some static hosts don't render `.md`; an explicit `.html` works
  everywhere.
- The Phase 7 landing page (separate Next.js + Nextra repo) will also be able
  to render the `.md` via MDX once it's built. Until then, the `.html` is
  the dev's path to a public URL.

## Step 6 — Fill Play Data Safety form

Overwrite `playstore/play_data_safety.md` with a filled version based on
Step 1's active-SDK list (don't blow away the template at `legal/play-data-safety.md`
— that stays as a reference). Use the form's actual questions verbatim:

```markdown
# Play Data Safety — <App Name>

(Generated <today's date>. Re-run /kit-generate-legal after toggling SDKs.)

## Data collection and security

- **Does your app collect or share any of the required user data types?**
  Yes <if anything in Step 1 is active, otherwise No>
- **Is all of the user data collected by your app encrypted in transit?**
  Yes
- **Do you provide a way for users to request that their data is deleted?**
  Yes — in-app via Settings → Account → Delete account, plus by emailing
  <contact email>. Data is fully purged within <retention window> days.

## Data types collected

For **each** active Play category, the form asks two per-type questions that
commonly confuse people. Here's how to answer them for this kit:

- **Is this data processed ephemerally?** → **No** for essentially everything
  this kit collects. "Ephemeral" means used only in memory for a single live
  request and never stored. This kit *stores* its data — auth in Supabase /
  Firebase, events in PostHog / Firebase Analytics, local data in Room /
  DataStore — so it is **not** ephemeral. Only answer **Yes** for a data type you
  read, use once in-flight, and never persist (rare here).
- **Is this data required, or can users choose?** → pick **"Data collection is
  required"** when the user can't turn it off (e.g. account email while auth is
  on — they must sign in to use the app). Pick **"Users can choose whether this
  data is collected"** only if your app exposes a real in-app opt-out for it.

<Render one row per active Play category below — only ones with an active
source. Use the two-question guidance above to fill the **Ephemeral** and
**Required** columns. Drop the email row if `AUTH_ENABLED = false`, the analytics
row if `ANALYTICS_ENABLED = false`, etc.>

| Data type | Collected | Processed ephemerally | Required, or users choose | Shared | Purpose | Source |
|---|---|---|---|---|---|---|
| Personal info → Email address | Yes | No | Data collection is required | No | Account management, Authentication | Supabase / Firebase / Google Sign-In |
| App activity → App interactions | <Yes/No> | No | <Required / Users can choose> | No | Analytics | PostHog, Firebase Analytics |
| <…one row per remaining active category…> | | | | | | |
```

## Step 6.5 — Generate the Data safety import CSV (one-click upload)

The kit ships **`data_safety_sample_reference.csv`** at the repo root — Google's official Data
safety **import format** (every `Question ID` / `Response ID` row the form supports). Fill it
from the **same active-SDK list** (Step 1) and write the result to
**`playstore/play_data_safety.csv`**, so the developer can import the whole form in one click and
so `/kit-publish-to-play` 5.6 has a file to import (generate it **here** — it always exists after
legal generation).

Read the template, and in the **`Response value`** column set **`true`** on exactly the rows the
app covers; leave the rest as the template has them:
- `PSL_DATA_COLLECTION_COLLECTS_PERSONAL_DATA` → `true` (anything active in Step 1; else `false`).
- `PSL_DATA_COLLECTION_ENCRYPTED_IN_TRANSIT` → `true`.
- **Account-creation method** (`PSL_SUPPORTED_ACCOUNT_CREATION_METHODS …`) → the row matching the
  auth survey — email/password → `PSL_ACM_USER_ID_PASSWORD`; Google/OAuth → `PSL_ACM_USER_ID_OTHER_AUTH`.
  Only if auth is on.
- **Per data type the active SDKs collect**, set the collection row **and** its purpose/sharing
  rows: email / name / avatar (auth), purchase history (RevenueCat), approximate location +
  device IDs (PostHog), crash logs + diagnostics (Crashlytics / Sentry). Drop any whose source
  SDK is off in Step 1.
- **Anything from Step 2 / Step 3** (services or captures you added by hand) → set its rows too,
  not just the kit SDKs. In particular: **audio / voice** → the *Audio → Voice or sound
  recordings* rows; **images/video** → *Photos and videos*; **files** → *Files and docs*;
  **precise location** → *Location → Precise*; **free-form text sent to an AI/transcription
  service** → the relevant content row **+ its "Data shared" row** (a third party like Deepgram,
  AssemblyAI, or OpenRouter receiving it counts as **shared**, not just collected). Never leave a
  hand-added audio/media/content flow out of the CSV.
- The **data-deletion** row → point at the hosted **privacy URL** (the policy describes the
  in-app delete flow).

Write the filled file to **`playstore/play_data_safety.csv`**.

> This CSV is a **best-effort draft from the SDK scan** — skim `playstore/play_data_safety.md`
> (the human-readable table) and adjust any row before importing.

## Step 7 — Print the Data Safety table + Play Console checklist

**First, print the data-safety table to the terminal** — render the exact same
`| Data type | Collected | Processed ephemerally | Required, or users choose |
Shared | Purpose | Source |` table you just wrote into `play_data_safety.md`,
with the real rows filled in. Many developers won't open the file; they should be
able to read the toggle answers straight from the terminal and fill the Play form.

Then print this verbatim block as the wrap-up:

> **Now in Play Console** — every item is a **row in Dashboard → "Set up your app"** (there is
> no "App content" menu in the current console):
>
> 1. **Set privacy policy** → paste the URL where you host `privacy_policy.html`.
> 2. **Data safety** → top-right **Import from CSV** → upload `playstore/play_data_safety.csv`
>    → review the **Preview** → **Submit**. (Use `playstore/play_data_safety.md` only as a
>    cross-check, or to fill the wizard by hand if an import ever fails.)
> 3. Set `KitConfig.PRIVACY_URL` to the hosted URL so **Settings → Privacy** opens it inside
>    the app.

Final reminder to the developer:

> Re-run `/kit-generate-legal` whenever you:
>
> - Toggle an SDK in `KitConfig` (auth on/off, analytics on/off, AI on/off).
> - Add a Retrofit interface or Supabase table.
> - Change the contact email or company name.
>
> Skipping the re-run means the published policy lies about what the app does
> — which is the exact failure mode Play strikes apps for.
