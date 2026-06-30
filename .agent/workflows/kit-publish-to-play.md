---
description: Publish your app to Google Play the right way — first release (signed build, app setup, closed testing's 12-tester / 14-day gate, production) or a fast update — in the correct order, no maze
---

You are running **`/kit-publish-to-play`** for NowKit.

Goal: take the developer's app from "built on my machine" to **live on Google Play**,
walking the Play Console **in the correct order** so they never hit the dependency maze
(privacy URL needs a hosted landing page, which needs screenshots; closed testing gates
production; etc.). Handles both a **first release** and a **fast update**.

Audience: first-time mobile developers / vibe coders. Most of this is manual Play Console
work that **can't** be automated — your job is to do the **local/code work for them**
(builds, version bumps, asset generation, surveys, the Data safety CSV) and then walk each
web step, **one at a time**.

## How to run this (read before starting)

- **Pace it — ONE sub-step at a time.** Present a step, then **wait for the developer to
  say "done"** before the next. **Never dump multiple Play Console screens at once** —
  they will lose their place. This is the #1 rule.
- **The Play Console steps here are transcribed from the real, current console — screen titles,
  button labels and radio-option wording are captured from actual screenshots.** When a block is
  quoted with `>`, present it to the developer **verbatim** — the exact titles/labels/options.
  **Do NOT paraphrase a step, reorder it, or swap in a menu path or label from your own memory.**
  Play's UI changes and your training data is stale (for example: there is **no "App content →
  …" menu** anymore — every task is a **row in the Dashboard "Set up your app" checklist**; the
  "App access" page is now **"Sign in details"**). If the real screen ever differs from the text,
  **trust the screen, do the step, and tell the developer the kit's wording looks outdated** —
  never silently invent a different path. Prose outside `>` blocks is instructions for *you*.
- A first release is **multi-day** — Phase 6 (closed testing) alone is **14 days**. Save
  progress by reading the project each run (see Resume check) and pick up where they left.
- **Docs:** https://kit.shipkaro.dev/docs/release

## Resume check — do this FIRST

A first release is **multi-day**, and one step (the screenshots' Gemini MCP) makes the
developer **restart Claude Code**, which ends the session. So progress is **persisted to a
file** — read it before greeting and resume, never restart.

**1. Read the durable progress file** `playstore/.publish-progress.md` if it exists. It
records the release type, the Phase 0 account-exempt decision, and which phases are done.
It's a **hint, not the source of truth** — always re-verify against the filesystem below
(disk wins on any conflict; e.g. if the AAB was deleted, rebuild it).

**2. Re-verify against the project** (also covers a project with no progress file yet — work
done before it existed):
- **App icon done?** `playstore/play_store_icon.png` exists **and** the `mipmap-*` launcher
  PNGs differ from the kit placeholder → Phase 1's icon step is done; skip it.
- **Signed AAB?** `ls app/build/outputs/bundle/release/app-release.aab`; `release.*` keys in
  `local.properties`.
- **Legal generated?** `ls playstore/privacy_policy.md playstore/play_data_safety.md`.
- **Listing copy?** `ls playstore/title.txt playstore/full_description.txt`.
- **Screenshots?** `ls playstore/screenshots/` (≥ 2 PNGs).
- **Gemini image MCP installed?** is a `generate_image` tool available — if yes, the MCP
  setup inside `/kit-generate-screenshots` is already done; don't reinstall.

**3. Ask only what code can't tell you** (Play Console state): *"Have you already created the
app on Play Console / uploaded a build to a track / started closed testing?"*

Reconcile all three, resume at the first unfinished phase, mark earlier ones done, and
**rewrite the progress file** to match.

## Survey the project (do this once, up front)

Read these so your guidance is accurate — never assume:
- **Base package + applicationId** → `app/build.gradle.kts` (`namespace`, `applicationId`).
- **Current version** → `app/build.gradle.kts` (`versionCode`, `versionName`).
- **Auth** → `KitConfig.kt` (`core/config/`): `AUTH_PROVIDER` (`SUPABASE`/`FIREBASE`/`STUB`=off),
  `GOOGLE_WEB_CLIENT_ID` (set = Google sign-in on).
- **Paywall** → `KitConfig.PAYWALL_ENABLED`, `ENTITLEMENT_ID`.
- **Remote config** → whether `RemoteAppConfig` is wired to a backend (Supabase/Firebase) vs LOCAL.
- **Ad / financial / health SDKs** → grep `app/build.gradle.kts` + `gradle/libs.versions.toml`
  for `play-services-ads`/AdMob/AppLovin, banking/crypto/lending/payment SDKs,
  `androidx.health`/Google Fit. (Kit ships none of these by default.)

## Progress tracking

A first release is multi-day, and the screenshots' Gemini MCP makes the developer restart
Claude Code (which ends the session) — so persist progress to a file. Maintain
`playstore/.publish-progress.md`: **write it when the path is chosen** (Step 1), **update it
at the end of every phase**, and **always update it right before any step that ends the
session** — i.e. before calling `/kit-generate-screenshots` (it may install an MCP and ask
for a restart). The Resume check reads it. Keep it short:

```markdown
# /kit-publish-to-play progress  (auto-written — safe to delete to start over)
release_type: first          # first | update
account_exempt: no           # yes | no | unknown  (the Phase 0 decision)

- [x] phase0  Play account + account type
- [x] phase1  App icon + version
- [ ] phase2  Create app on Play Console
- [ ] phase3  Listing assets + legal + host (screenshots, ASO, legal, landing)
- [ ] phase4  Build signed AAB + internal testing
- [ ] phase5  Set up your app (11-task checklist)
- [ ] phase6  Closed testing (12-tester / 14-day)
- [ ] phase7  Production release

last_step: phase3 — screenshots; Gemini MCP installed, awaiting restart
```

The `last_step` line is a free-text breadcrumb — make it specific enough to resume from
(which phase, which sub-step, what you were waiting on).

---

## Step 1 — First release or update?

Ask the developer (wait for their answer): *"Is this your app's first release, or an update
to an app that's already on Google Play?"*
- **First release (never published)** → do the **First-release path** below (Phases 0–7).
- **Update (already on Play)** → jump to the **Update path** (much shorter) in part 5.

---

# First-release path

## Phase 0 — Play account ready + account type (decides the whole path)

**0.1 — Account exists?** Ask the developer (wait for their answer): *"Do you have a verified
Google Play Console account?"*
- **Yes** → continue to 0.2.
- **No** → tell them to create one at https://play.google.com/console (one-time **$25**
  fee) and complete identity verification, then resume. Stop here.

**0.2 — Account type (ask this NOW — it decides whether the 14-day closed-testing gate
applies).** Ask the developer (wait for their answer): *"What kind of Play Console account is
this?"*
- **Organisation account** → **exempt** from the closed-testing requirement.
- **Personal account created BEFORE 13 Nov 2023** → **exempt**.
- **Personal account created ON/AFTER 13 Nov 2023** → **not exempt** — must run closed
  testing (12 testers / 14 continuous days) before applying for production.

**Branch on the answer — set this for the rest of the run:**
- **Exempt** → there is **NO closed-testing gate**. The path is shorter:
  Phases 1 → 2 → 3 → 4 → 5 → **straight to Phase 7 (production)**. **Skip Phase 6
  entirely** (don't present the 14-day flow, it'll just confuse them). Tell the developer
  plainly: *"Your account is exempt, so you can publish to production right after the 'Set
  up your app' checklist — no 14-day test needed."*
- **Not exempt** → the **Phase 6 closed-testing gate applies** (the 12-tester / 14-day
  long pole).

Carry this exempt/not-exempt decision through the whole run.

---

**Continued in `kit-publish-to-play-part-2.md` (Phases 1, 2).**
