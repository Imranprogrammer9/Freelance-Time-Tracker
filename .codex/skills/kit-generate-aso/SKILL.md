---
name: kit-generate-aso
description: Generate Play Store listing copy (title, short + long description) from a keyword strategy
---
You are running **`/kit-generate-aso`** for NowKit. Goal: produce
conversion-optimised **Google Play Store listing copy** — app name, short
description, long description — built around a confirmed keyword strategy, and
write them as plain `.txt` files the developer can copy-paste into Play Console.

Audience: first-time mobile developers / vibe coders. Be brief; do not paste
walls of text.

This command is **standalone** — run it anytime to (re)write your listing copy.
`/kit-upload-on-google-play` Step F invokes it inline. The actual generation is
done by the `aso-googleplay-listing` skill shipped with the kit; this command
just drives it and handles the keep/regenerate branch.

## Step 0 — Detect existing state

Check for existing listing files:

- `playstore/title.txt`
- `playstore/short_description.txt`
- `playstore/full_description.txt`

Branch:

- **All three exist** — Ask the user (wait for their answer):
  - **Keep as-is** — show the current app name + short description (first line of
    each file) and exit.
  - **Regenerate** — run the full flow below, overwriting.
  - **Tweak one** — ask which field, regenerate just that one with the skill,
    keep the others.
- **Some or none exist** — walk the full flow.

## Step 1 — Generate via the skill

Invoke the **`aso-googleplay-listing`** skill and let it drive. It will:

1. Gather inputs — app concept (reuse the answer from `/kit-start-setup` if the
   developer already gave it, don't re-ask), audience, top features, brand tone.
2. Build a **keyword strategy** and confirm which keyword groups to target with
   the developer (grouped multi-select).
3. Write **app name** (≤30), **short description** (≤80), and **long
   description** (≤4000) with the chosen keywords baked into natural prose.
4. Show all three with character counts and loop until the developer approves.
5. Write the three files:
   - App name → `playstore/title.txt`
   - Short description → `playstore/short_description.txt`
   - Long description → `playstore/full_description.txt`

## Step 2 — Wrap up

After the skill writes the files, confirm the three paths and remind the
developer:

> Paste each file into Play Console → **Main store listing**:
> - `playstore/title.txt` → App name
> - `playstore/short_description.txt` → Short description
> - `playstore/full_description.txt` → Full description

Don't run any build — listing copy is text, no compile needed.

Mention they can also generate a matching marketing page with
`/kit-generate-landing` (hero + features + privacy + terms + contact).
