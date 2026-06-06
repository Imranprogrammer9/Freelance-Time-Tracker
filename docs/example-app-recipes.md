# Example app recipes

Five complete walkthroughs that take a real app idea from `/kit-start-setup` all the
way to Google Play. Each one uses a **different mix** of the kit's features, so whatever
you're building, one of these is close to your path.

Every recipe is just the choices you make when the kit asks — you don't write any
boilerplate. Pick the one nearest your app, follow the choices, swap in your own name,
color, and screens.

> New to the kit? Run `/kit-start-setup` first and let it walk you through. These recipes
> show the *choices*, not new commands. Full docs: https://kit.shipkaro.dev/docs

## Pick the recipe closest to your app

| Recipe | App | Sells? | Login? | Best when your app… |
|--------|-----|--------|--------|---------------------|
| **A** | Water Tracker | Free | No | works fully offline, no account needed |
| **B** | Habit Tracker | Free | Google | is free but you want accounts + AI features |
| **C** | Calm Meditate | Subscription | Email + Google | charges before use (hard paywall), uses Firebase |
| **D** | QuickNotes | Subscription | Email | charges but lets people try first (soft paywall), all-Supabase |
| **E** | Everything | — | Email + Google | you want to see every feature turned on |

Mix and match freely — these are starting points, not rules.

---

## Recipe A — Water Tracker  *(free · no login · offline)*
The simplest possible app: no accounts, no payments, works on a plane. Great first build.

Run `/kit-start-setup` and choose:

| Step | Choice |
|------|--------|
| What are you building? | **A free app** |
| Rename | `com.you.watertracker` · **Water Tracker** |
| Onboarding | *"Track your daily water intake and build a hydration streak."* |
| Brand & theme | Blue `#2196F3` · **Material** icons |
| Authentication | **Skip — no login** |
| Paywall | *(auto-skipped — it's free)* |
| Analytics | **None** |
| Build & run | ✔ |

Then build your screen and ship:
1. `/kit-design-app` → a water-log screen (tap to add 250 ml, daily total, streak). Store it in **Room** (on-device).
2. `/kit-generate-legal` → privacy policy + Play Data Safety.
3. `/kit-generate-screenshots` → Play Store screenshots from your app.
4. `/kit-upload-on-google-play` → ship it.

**What you'll see:** splash → onboarding → straight to Home. No login screen, no paywall.

---

## Recipe B — Habit Tracker  *(free · Google login · AI · personalised onboarding)*
Free to use, but people sign in so their habits sync, and your AI suggests new habits.

Run `/kit-start-setup` and choose:

| Step | Choice |
|------|--------|
| What are you building? | **A free app** |
| Rename | `com.you.habits` · **Habitly** |
| Onboarding | *Stop and run `/kit-design-onboarding`* → a personalised quiz (goal, reminder time, motivation) |
| Brand & theme | Green `#22C55E` · **Feather** icons |
| Authentication | **Supabase · Google only** |
| Paywall | *(auto-skipped — it's free)* |
| Analytics | **PostHog + Crashlytics** |
| Build & run | ✔ |

Then add the extras:
1. `/kit-setup-ai` → OpenRouter, so the app can suggest a new habit with AI.
2. `/kit-setup-review-dialog` → ask for a Play review **after a key action** (e.g. 3rd habit done).
3. `/kit-design-app` → habit list + add + streak + done-today. Store in **Supabase**.
4. `/kit-plan-release-analytics` → set up a funnel (onboarding → first habit → retention).
5. `/kit-upload-on-google-play` → ship it.

**What you'll see:** a personalised quiz onboarding → Google sign-in (native sheet, not a browser) → Home. No paywall. AI button suggests a habit.

---

## Recipe C — Calm Meditate  *(subscription · hard paywall · Firebase)*
A premium app: people must subscribe before they can use it. Built on Firebase.

Run `/kit-start-setup` and choose:

| Step | Choice |
|------|--------|
| What are you building? | **A paid / subscription app** |
| Rename | `com.you.calmmeditate` · **Calm Meditate** |
| Onboarding | *"Guided meditations and sleep sounds for a calmer mind."* |
| Brand & theme | Purple `#7C3AED` · **Tabler** icons |
| Authentication | **Firebase · Email + Google** *(adds `google-services.json`)* |
| Paywall | **RevenueCat · HARD** (blocking — no skip) |
| Analytics | **Firebase Analytics + Crashlytics + PostHog** |
| Build & run | ✔ |

Then:
1. `/kit-setup-updates` → remote config on **Firebase** (lets you push an update prompt or maintenance notice later).
2. `/kit-generate-legal` → privacy policy + Data Safety (covers payments + Firebase).
3. `/kit-upload-on-google-play` → ship it.

**What you'll see:** onboarding → Firebase login (email form + Google) → **paywall you can't skip** → Home only after subscribing. Subscribers skip the paywall next time.

---

## Recipe D — QuickNotes  *(subscription · soft paywall · all-Supabase)*
Charges money, but lets people try first (soft paywall they can dismiss). Everything runs on
one Supabase project — login, settings, and your notes data.

Run `/kit-start-setup` and choose:

| Step | Choice |
|------|--------|
| What are you building? | **A paid / subscription app** |
| Rename | `com.you.quicknotes` · **QuickNotes** |
| Onboarding | *"Fast notes that sync everywhere."* |
| Brand & theme | Amber `#F59E0B` · **Material** icons |
| Authentication | **Supabase · Email only** |
| Paywall | **RevenueCat · SOFT** (shows "Maybe later") |
| Analytics | **Sentry** |
| Build & run | ✔ |

Then:
1. `/kit-setup-updates` → remote config on **Supabase** (run the SQL it prints to create the config table).
2. `/kit-design-app` → notes list + editor. Store in **Supabase** (same project as login).
3. `/kit-upload-on-google-play` → ship it.

**What you'll see:** onboarding → email sign-in → **paywall with a "Maybe later"** → Home. Later you can flip on a maintenance screen or push an update prompt from your Supabase config table.

---

## Recipe E — Everything on  *(see the whole kit)*
Not a real app — a tour. Turn on every feature so you can see what's included before you decide
what your real app needs.

Run `/kit-start-setup`, pick **"Just exploring the kit"**, then say yes to everything:
Supabase **Email + Google**, **HARD** paywall, **all four** analytics (PostHog + Firebase +
Crashlytics + Sentry), a custom icon pack (**Brand & theme → Pick another → Simple Icons**).

Then:
1. `/kit-setup-ai` → OpenRouter.
2. `/kit-translate` → add languages (try **Arabic / Urdu** for right-to-left, plus **Hindi**).
3. `/kit-setup-review-dialog` → ask for a review on the 5th launch.
4. On Home, tap **Browse kit components** → scroll the **Components catalog** to see every UI piece in your colors.

**What you'll see:** the full flow — onboarding → login → paywall → Home — plus a catalog of all
40+ components. Switch your phone to Arabic and the layout flips right-to-left automatically.

---

## Things to check on your device
After any recipe, a quick sanity pass:

- Only **one** splash (Android's own) — the app doesn't show a second.
- A **free / no-login** app goes straight to Home — no login or paywall screens.
- A **hard** paywall can't be dismissed; a **soft** paywall has "Maybe later".
- After subscribing (or "Restore"), the paywall doesn't show again.
- **Sign out** from anywhere sends you back to the login screen.
- **Delete account** works and lands you back on login (no blank screen).
- Your **app icon** is your brand mark and the launcher background is your brand color.
- **Settings → Open-source licenses** lists the libraries (not blank).
- If you only ship one language, the **Language** row in Settings is hidden.

> Re-running a setup command is safe — it notices what you already configured and offers to keep
> it, change one thing, or start that piece over. You won't be asked everything twice.
