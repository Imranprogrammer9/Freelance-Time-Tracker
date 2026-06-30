---
description: kit-publish-to-play (part 3 of 5) — listing assets + legal + host, signed build + internal testing
---

Continued from kit-publish-to-play.md.

## Phase 3 — Listing assets + legal + host (generates the privacy/terms URLs)

The build (Phase 4) and the "Set up your app" checklist (Phase 5) both need finished assets
and a **public privacy URL**. Generate + host them now — **before** the build, so the app's
in-app Settings links are baked in.

**3.1 — Screenshots (REQUIRED — do not defer).** **First update `playstore/.publish-progress.md`**
(mark phases 0–2 done, phase3 in-progress) — `/kit-generate-screenshots` may install the
Gemini MCP and ask the developer to **restart Claude Code**, which ends this session, and the
progress file is how you pick up here afterwards. Then call **`/kit-generate-screenshots`**
→ `playstore/screenshots/`. These are **not optional**: the store listing (5.11) won't save
without **≥ 2 phone screenshots**, and the listing is part of the "Set up your app"
checklist that **gates the closed-testing and production tracks**. If the developer doesn't
want generated ones, they must drop their own PNGs into `playstore/screenshots/` now. After a
restart, re-running `/kit-publish-to-play` lands you back here via the Resume check.

**3.2 — Listing copy.** Call **`/kit-generate-aso`** → `playstore/title.txt`,
`short_description.txt`, `full_description.txt` (used in 5.11).

**3.3 — Legal content.** Call **`/kit-generate-legal`** → `playstore/privacy_policy.md`
+ `.html`, terms, and `playstore/play_data_safety.md` (the Data safety answers, used in 5.6).

**3.4 — Landing page (hosts privacy + terms → public URLs).** Call **`/kit-generate-landing`**.
It builds + hosts the static site and produces the styled **`privacy.html`**,
**`terms.html`**, and an unlisted **`data-safety.html`**. Capture the public URLs:
- Privacy: `https://<site>/privacy.html` → used in 3.5 + 5.1
- Terms: `https://<site>/terms.html` → used in 3.5 + by a pre-registration reward later
- Data safety (unlisted): `https://<site>/data-safety.html` → handy reference for 5.6

**3.5 — Write the URLs into the app (so Settings links work).** Set
`KitConfig.PRIVACY_URL` and `KitConfig.TERMS_URL` (`core/config/`) to the hosted URLs from
3.4. The in-app **Settings → Privacy policy / Terms** entries open these — they must be in
the code **before** Phase 4 builds the AAB, or the shipped app links to the placeholders.

**3.6 — Plan release analytics.** Call **`/kit-plan-release-analytics`** (don't ask
permission — a funnel is load-bearing for "did this launch work?"). It wires 3–5 release
events into the code.

## Phase 4 — Build the signed AAB + put it on internal testing

**4.0 — Paywall billing readiness (only if the app sells — do this BEFORE the build).** From the
Survey: if `PAYWALL_ENABLED = false`, **skip this** (free app, no billing). If
`PAYWALL_ENABLED = true`:
- **Verify the billing permission is declared:** `app/src/main/AndroidManifest.xml` must have an
  **uncommented** `<uses-permission android:name="com.android.vending.BILLING" />`. If it's still
  commented, **uncomment it now** — Play won't unlock product creation without it in the uploaded
  build, and the build must carry it. Present verbatim to confirm the dashboard side:
> **You have a paywall — is billing wired up?** Before real users can pay you need:
> 1. **Products** (one-time and/or subscription) created **+ activated** in Play Console.
> 2. **A service-account JSON** uploaded to RevenueCat (~24–36 h to propagate — the RevenueCat ↔
>    Play connection should show **Verified**).
> 3. **An offering + published paywall** in RevenueCat.
> 4. The build **on a testing track + the tester opt-in URL opened** on your device — Play only
>    serves products to a build on a track, to opted-in testers (the internal-testing upload
>    below puts it on a track; just open the opt-in URL afterwards or products stay empty).
>
> Not done yet? Full guide: **https://kit.shipkaro.dev/docs/paywall** (or `/kit-setup-paywall` →
> "Set up products + Play billing"). Don't block the build on it — but finish billing on the test
> track before promoting to production.

**4.1 — Build the signed AAB.** Call **`/kit-sign-release`** — it creates the release
**keystore** (first time; remind them to back it up — losing it means they can never update
the app) and builds the **signed** `app-release.aab`, now carrying the **new icon (Phase 1)
and the privacy/terms URLs (Phase 3.5)**.

> ✅ Signed build at `app/build/outputs/bundle/release/app-release.aab`.

**Then put it on internal testing.** This is **instant, no review**, registers your package
on Play, and (for paid apps) confirms the RevenueCat ↔ Play connection. **Skip if a build is
already on a track** (e.g. the placeholder `/kit-setup-paywall` uploaded). Otherwise, one
sub-step at a time:

**4.2 — Select testers** (present, wait):
> Play Console → your app → **Test and release → Testing → Internal testing → Testers**
> tab. Tick a tester list, or **Create email list** and add emails. **Save**. (Up to 100;
> the join link appears after you publish.)

**4.3 — Create the release** (present, wait):
> **Create new release** (top-right). App signing by Google is on automatically. Under
> **App bundles**, **Upload** `app/build/outputs/bundle/release/app-release.aab`. Set a
> **Release name** (auto-filled is fine) + optional notes. **Next**.

**4.4 — Roll out** (present, wait):
> Review the preview (a "no debug symbols" warning is fine to ignore). **Save → Start
> rollout to Internal testing**. The track goes **Active** with your release live.

---

**Continued in `kit-publish-to-play-part-4.md` (Phase 5).**
