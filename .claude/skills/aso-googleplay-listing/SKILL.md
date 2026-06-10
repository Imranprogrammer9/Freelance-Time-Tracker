---
name: aso-googleplay-listing
description: Generate a conversion-optimised Google Play Store listing (app name, short description, long description) with keyword strategy. Use when the user asks to write or improve Play Store listing copy, ASO copy, store listing, app title, short description, long description, app store optimization for Android / Play Store. Skip for iOS App Store / Apple — there is a separate skill for that.
---

You are running the **aso-googleplay-listing** skill — generate
conversion-optimised Google Play Store listing copy for a NowKit app.

## Play vs App Store — important

Google Play has **no** subtitle field, **no** dedicated keywords field, and
**no** promotional text. Play SEO is driven by:
- **App name** (highest weight) — max 30 chars.
- **Short description** (high weight) — max 80 chars, shown above the fold.
- **Long description** (volume keyword discovery) — max 4000 chars.

So your job is to bake keywords into the natural prose of these three fields,
without stuffing.

## Step 1 — Gather inputs

Ask the developer for these in plain conversational text (free-form — do NOT
use a multi-choice tool unless noted):

1. **App concept** — one sentence. What does it do?
2. **Target audience** — who is it for? (e.g. "indie devs shipping their first
   app", "habit-trackers who failed at journaling", "small-business owners
   tracking invoices")
3. **Top 3 core features** — bulleted.
4. **Brand tone** — ask with **AskUserQuestion**:
   - **Minimal** — clean, calm, professional.
   - **Friendly** — warm, conversational, human.
   - **Bold** — confident, punchy, opinionated.
   - **Playful** — fun, light, character-driven.
5. **Keyword seeds (optional)** — any specific search terms they want to rank
   for? Skip if they don't know — you will derive them.

If the developer already ran `/kit-start-setup` and answered the "what is your
app about" question, reuse that answer for input 1 instead of re-asking.

## Step 2 — Keyword strategy

Before writing copy, build a small keyword set:
- 1 **primary keyword** — the single most important search term for this app.
- 3–5 **supporting keywords** — related terms users would search for.

Pull from the seeds (if any), the audience description, the core features, and
common Play search behaviour. Prefer specific over generic ("offline habit
tracker" beats "tracker").

**Cluster the keywords into 2–4 named groups** by search intent — e.g. for a
habit app: *Habit & streaks* (`habit tracker`, `daily streaks`, `routine
builder`), *Productivity* (`goal tracker`, `self improvement`), *Offline /
privacy* (`offline habit tracker`, `no account needed`). Each group is one way
users would search for this app.

**Confirm which groups to target** with **AskUserQuestion** (`multiSelect: true`):
one option per group — label = group name, description = the keywords inside it.
The developer ticks the groups that match how they want to be found. Phrase the
question so the strongest group reads as the recommended default.

From the **selected** groups, lock:
- 1 **primary keyword** — the single strongest term across the picked groups.
- 3–5 **supporting keywords** — the rest, drawn only from picked groups.

Echo the final plan in one line, then continue to Step 3:

> **Targeting:** primary `<primary>` · supporting `<k1>`, `<k2>`, `<k3>`, …

If the developer ticks no group (or "Other"), fall back to deriving 1 primary +
3 supporting keywords yourself and show them for a quick yes/no.

## Step 3 — Generate the three fields

Write all three fields together — they reinforce each other.

### App name — max 30 chars

Format: `<Brand>: <benefit-or-primary-keyword>` OR `<Brand> — <benefit>` OR
just `<Brand>` if the brand IS the keyword.

Rules:
- Hit the primary keyword if it fits naturally.
- Brand first; supplemental keyword phrase after a colon / em-dash.
- No emojis, no SHOUTING CASE, no claims like "#1" or "Best".
- Count UTF-16 chars; warn if > 30.

### Short description — max 80 chars

One sentence. Hook + benefit + (subtle) keyword. Should answer "why install
this?" Action verbs preferred. Avoid filler ("Welcome to..."). Count chars.

### Long description — max 4000 chars

Structure:

```
<one-line hook — restates the short description with energy>

<2-3 sentence problem statement — what pain does this solve?>

KEY FEATURES
• <feature 1 — outcome-focused, with keyword>
• <feature 2 — outcome-focused, with keyword>
• <feature 3 — outcome-focused, with keyword>
(more features only if real and useful)

WHY <BRAND>?
<3-4 short paragraphs covering: who it's for, how it differs from the
obvious alternative, the "moment" when users feel the value, a closing
line that nudges install>

<single closing CTA line>
```

Rules:
- Embed every supporting keyword naturally at least once.
- Primary keyword appears 2–3 times in the body — never stuffed.
- Plain text. No Markdown bold / italics (Play strips most formatting; bullet
  • renders fine). No emojis unless the tone is Playful and the developer
  explicitly wants them.
- Keep paragraphs short — Play listings are skimmed.
- Localisation note — if a Brand or feature name is the same in Urdu, the
  developer can paste the same copy into a `values-ur/` equivalent later
  (not your concern here).

## Step 4 — Show + refine

Show all three fields together with their char counts, e.g.:

```
APP NAME   (28/30):  HabitFlow: Daily Habit Tracker
SHORT      (74/80):  Build streaks, beat slumps, stay consistent — offline habits that stick.
LONG     (1842/4000):
<full text...>
```

Ask the developer to **approve** or call out a change (tone, length, keyword
emphasis, brand mention, etc.). Refine and re-show. Loop until they approve.

## Step 5 — Write files

When approved, write the three files (overwriting any existing content):

- `playstore/title.txt` — app name.
- `playstore/short_description.txt` — short description.
- `playstore/full_description.txt` — long description.

Each file is **plain text only** — no surrounding quotes, no Markdown wrappers,
no trailing newlines except a single one at end of file.

Report success with the three file paths and remind the developer to paste
each into Play Console → Main store listing.

## Notes for the model

- This skill is Play-specific. If the user asks for App Store / iOS copy,
  decline and refer them to the iOS skill.
- Never invent features the developer did not mention.
- Never write claims that need substantiation ("Most downloaded", "Award-
  winning") unless the developer confirms it is true and they can defend it.
- Default localisation = English (`en-US`). If the developer wants another
  default locale, swap the path's locale folder accordingly.
