---
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

## Step 0 — Detect existing legal docs

If `playstore/privacy_policy.html` and `playstore/play_data_safety.md` already
exist, ask the user (wait for answer) to keep them (exit) or regenerate (re-scan +
overwrite). Otherwise continue to Step 1. Don't silently overwrite.

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

## Step 2 — Scan custom endpoints

Grep `app/src/main/java` for:

- `@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH` annotations (Retrofit interfaces).
- `client.from("…")` calls (Supabase Postgrest table accesses).

Build a list of every endpoint the dev's own code talks to (skip the
OpenRouter and Supabase auth interfaces — those are kit-provided).

If the list is empty, skip Step 3's endpoint sub-question.

If the list is non-empty, show it back to the developer and ask:

> I found these endpoints / tables your app talks to:
>
> - <list each path>
>
> For each, does the request body include personal data (email, name, free-form
> user text, location, photos)? Pick one per endpoint: (a) personal data, (b)
> anonymous usage only, (c) skip — not user data.

Record the answers; they feed Step 4's "Data we collect" section.

## Step 3 — Ask legal questions

Ask the user (wait for their answer) to collect the following — one screen per
question, batched where it makes sense:

1. **Company / individual name** — appears as "we" in the policy.
2. **Contact email** — where users send privacy questions. Required.
3. **Jurisdiction** — country / state. Affects which laws to call out
   (GDPR / CCPA / LGPD / DPDPA).
4. **Target users in EU?** → triggers GDPR section.
5. **Target users in California?** → triggers CCPA section.
6. **Target users under 13?** → triggers COPPA + Play family-policies warning.
7. **Data retention window** — how long after account deletion is data fully
   purged (default 30 days).
8. **Server region** — where their backend hosts data (matters under GDPR /
   data-residency claims).

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

## Step 7 — Print the Data Safety table + Play Console checklist

**First, print the data-safety table to the terminal** — render the exact same
`| Data type | Collected | Processed ephemerally | Required, or users choose |
Shared | Purpose | Source |` table you just wrote into `play_data_safety.md`,
with the real rows filled in. Many developers won't open the file; they should be
able to read the toggle answers straight from the terminal and fill the Play form.

Then print this verbatim block as the wrap-up:

> **Now in Play Console:**
>
> 1. **App content → Privacy policy** → paste the URL where you host
>    `privacy_policy.html`.
> 2. **App content → Data safety** → open `playstore/play_data_safety.md`
>    side-by-side and fill the web form using its answers.
> 3. **App content → Data deletion** → point at a section of the hosted
>    privacy policy that describes the in-app delete flow (the policy already
>    explains it under "Your rights").
> 4. Set `KitConfig.PRIVACY_URL` to the hosted URL so Settings → Privacy
>    opens it inside the app.

Final reminder to the developer:

> Re-run `/kit-generate-legal` whenever you:
>
> - Toggle an SDK in `KitConfig` (auth on/off, analytics on/off, AI on/off).
> - Add a Retrofit interface or Supabase table.
> - Change the contact email or company name.
>
> Skipping the re-run means the published policy lies about what the app does
> — which is the exact failure mode Play strikes apps for.
