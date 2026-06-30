---
description: kit-publish-to-play (part 5 of 5) — closed testing's 12-tester / 14-day gate, production, the fast Update path, wrap up
---

Continued from kit-publish-to-play.md.

## Phase 6 — Closed testing (the 12-tester / 14-day gate)

**Only for NOT-exempt accounts** (decided back in Phase 0.2). **If the account is exempt,
you already skipped this — go straight to Phase 7.** Do not run the 14-day flow for an
exempt account.

For a not-exempt account, closed testing is **required** before production — a **14-day**
process. Two prerequisites:
- **"Set up your app" must be 11/11 (Phase 5)** — including the **store listing with
  screenshots**. Closed testing won't start until the checklist is complete (the Closed
  testing card literally says *"finish setting up your app"* first).
- The app you closed-test must be your **real app** (build your core features first with
  `/kit-design-app`) — an empty shell can be rejected in review.

> ⏰ This is the long pole — start it as soon as Phase 5 is done and keep polishing meanwhile.

**6.1 — Set up the closed test track** (Dashboard → Closed testing → tasks): **Select
countries and regions**, then **Select testers** — add a list with **≥ 12 testers**. Each
tester must **open the opt-in link and accept** to count. (Cohort tip: shared tester pool.)

**6.2 — Create + roll out the release:** **Create new release** → upload the AAB (or
**promote** your internal build) → name + notes → **Preview** → **Send the release to
Google for review** (closed testing IS reviewed).

**6.3 — The 14-day clock** (Dashboard → Production → *Apply for access to production*):
three criteria with live progress —
> 1. **Publish a closed testing release** ✓
> 2. **Have at least 12 testers opted-in** (*"N testers currently opted-in"* — need ≥ 12)
> 3. **Run your closed test with ≥ 12 testers for ≥ 14 days** (*"12 testers have been
>    opted in for X days continuously"*)
- It must be **12+ opted-in CONTINUOUSLY for 14 days** — if it drops below 12, continuity
  can reset. The **Apply for production** button stays greyed until day 14, then turns blue.

**6.4 — Apply for production:** click **Apply for production** → answer the questions about
your closed test → **submit**. Status: *"reviewing your application… usually 7 days or
less."* When granted: *"Congratulations! Your app has been granted production access."*

## Phase 7 — Production release (go live)

Call **`/kit-generate-changelog`** for the "What's new" notes, then present verbatim:

> **Publish to production:**
> 1. **Production → Create new release**.
> 2. Upload the signed AAB (or **promote** your closed-testing build).
> 3. Paste the **What's new** notes from `playstore/changelogs/<versionCode>.txt`.
> 4. **Preview → Review release → Start rollout to Production** (full, or a staged % you
>    ramp up).

**Then submit everything for review (this is the step people miss).** All your saved tasks +
this release sit as **pending changes** until you submit them in one batch:
> Left sidebar → **Publishing overview** → under **"Changes not yet submitted for review"**
> click **Send app for review**. Play runs quick checks first (a few minutes — "Up to N
> minutes remaining") and sends it once they pass. This is where all the **"Not now"** dialogs
> from Phase 5 finally get submitted, together.

(For an **exempt** account that skipped Phase 6, this Publishing-overview submit *is* your
production submission — there's no separate "apply for production" gate.) After Google's review
clears, the app is **live on Google Play**. 🎉

---

# Update path (app already on Google Play)

Short flow — no closed-testing gate, no "Set up your app" (already done). **One step at a
time, waiting for "done".**

**U1 — Bump the version (do this BEFORE building).** Every build uploaded to Play needs a
**unique, higher `versionCode`** — Play rejects a duplicate (the #1 upload snag). In
`app/build.gradle.kts`: **increment `versionCode` by 1**, and bump `versionName` to the
next release string (ask the developer, e.g. `0.1.0 → 0.1.1`).

**U2 — Regenerate legal if your SDKs changed.** Ask: *"Since your last release, did you toggle
any SDK — turn the paywall on/off, add/remove analytics, enable AI, change auth?"* (cross-check
`KitConfig` if unsure). If **yes**, your privacy policy + **Play Data Safety** are generated from
which SDKs are active, so they're now **stale** — and Play strikes apps whose Data Safety doesn't
match real behaviour. Call **`/kit-generate-legal`** to regenerate `privacy_policy.*` +
`playstore/play_data_safety.csv`, then **re-host the privacy page** (call
**`/kit-generate-landing`**, or re-upload `privacy_policy.html` to the same URL) and **re-import
the new `play_data_safety.csv`** on the Play **Data safety** row. If **no SDKs changed**, skip.

**U3 — Release notes.** Call **`/kit-generate-changelog`** → writes the user-facing
"What's new" to `playstore/changelogs/<versionCode>.txt` from git history.

**U4 — Screenshots (only if the UI changed).** If this update changed screens, call
**`/kit-generate-screenshots`** to refresh `playstore/screenshots/`. Otherwise skip.

**U5 — Plan release analytics (if there's something new to measure).** If this update ships a
feature worth tracking (or you never wired a funnel), call **`/kit-plan-release-analytics`** to
add the events + funnel **before the build**. Skip if nothing new to track.

**U6 — Remote config (if wired).** If `RemoteAppConfig` is on a backend (not LOCAL), remind
the developer they can bump the **force/soft-update version** + **changelog** there so
existing users get the update prompt — **no app change needed**. Skip if LOCAL.

**U7 — Build the signed AAB.** Call **`/kit-sign-release`** (reuses the existing
keystore) → fresh `app-release.aab` with the new `versionCode`.

**U8 — Upload.** Present verbatim:
> **Upload the update in Play Console:**
> 1. Open your app → the track you ship from (**Production**, or a testing track).
> 2. **Create new release** → upload `app/build/outputs/bundle/release/app-release.aab`.
> 3. Paste the release notes from `playstore/changelogs/<versionCode>.txt`.
> 4. If you refreshed screenshots (U4), upload them on the store listing.
> 5. **Save → Review release → Start rollout**.

After review clears, the update is live.

## Wrap up

State plainly: which path/phase they finished, what's next (often "wait out the 14-day
closed test, then Apply for production", or "rollout is live"), and that **open testing**
and **pre-registration** are optional extras. Keep it to a few lines.
