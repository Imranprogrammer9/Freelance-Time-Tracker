# Example Recipes

A recipe is a **complete, real app** taken from `/kit-start-setup` all the way to Google
Play — showing the exact choices you make at each question. Whatever you're building, one
recipe is close to your path. Copy its choices, swap in your name, color, and screens.

**Why recipes exist:** the kit asks you a lot of yes/no/which questions during setup. Each
combination produces a different app (free vs paid, login vs none, hard vs soft paywall,
Supabase vs Firebase). Recipes are pre-made answer sheets for the common combinations, so
you're never guessing what to pick.

> Recipes don't introduce new commands — they show the *choices* inside the commands you
> already have. Run `/kit-start-setup` and follow a recipe's table.

---

## Pick the recipe closest to your app

| Recipe | App | Sells? | Login? | Best when your app… |
|--------|-----|--------|--------|---------------------|
| **A** | Water Tracker | Free | No | works fully offline, no account needed |
| **B** | Habit Tracker | Free | Google | is free but you want accounts + AI features |
| **C** | Calm Meditate | Subscription | Email + Google | charges before use (hard paywall), uses Firebase |
| **D** | QuickNotes | Subscription | Email | charges but lets people try first (soft paywall), all-Supabase |
| **E** | Everything | — | Email + Google | you want to see every feature turned on |

Mix and match freely — these are starting points, not rules.

**→ Full choice tables + "what you'll see on device" for each recipe:
[example-app-recipes.md](example-app-recipes.md)**

---

## What each recipe teaches

### A — Water Tracker · *free · no login · offline*
The simplest possible build. No accounts, no payments, works on a plane. Data in **Room**
(on-device). Teaches the **bare minimum path**: rename → theme → skip auth → build → ship.
Great first app.

### B — Habit Tracker · *free · Google login · AI · personalised onboarding*
Free to use, but people sign in so data syncs, and AI suggests new habits. Teaches
**Supabase Google-only auth**, the `/kit-design-onboarding` **questionnaire**, `/kit-setup-ai`,
`/kit-setup-review-dialog`, and a **PostHog funnel**.

### C — Calm Meditate · *subscription · hard paywall · Firebase*
A premium app — you must subscribe before using it. Teaches a **HARD (blocking) paywall**,
the **Firebase** stack end-to-end (auth + analytics + remote config), and how
`/kit-generate-legal` covers payments + Firebase in your privacy policy.

### D — QuickNotes · *subscription · soft paywall · all-Supabase*
Charges money but lets people try first (a **SOFT paywall** they can dismiss). Everything —
login, settings, notes data — on **one Supabase project**. Teaches the soft-paywall pattern,
Supabase as both auth and data, and **Supabase remote config** (run the SQL it prints).

### E — Everything on · *the full tour*
Not a real app — a tour. Turn on **every** feature so you can see what's included before you
decide. Supabase email+Google, HARD paywall, **all four** analytics, OpenRouter AI, RTL
languages via `/kit-translate`, and the **Components Catalog** in your brand colors.

---

## How to use a recipe

1. Open the [full recipe](example-app-recipes.md) closest to your app.
2. Run `/kit-start-setup`.
3. When it asks a question, answer with the recipe's choice (swap in your own name, package,
   color).
4. After setup, run the recipe's follow-up commands (e.g. `/kit-setup-ai`, `/kit-design-app`).
5. Run the **device sanity checklist** at the bottom of the recipes file before you ship.

---

## A note for maintainers

The five recipes are deliberately **orthogonal** — together they exercise every branch of
the kit exactly once (free/paid, no-auth/Supabase/Firebase, hard/soft paywall, Room/Supabase
data, each analytics provider, AI on/off, simple vs questionnaire onboarding). So the recipe
set doubles as a **manual device-test matrix**: run all five and you've tested every path.

**Next:** [Components Catalog](components.md) — the building blocks every recipe uses.
