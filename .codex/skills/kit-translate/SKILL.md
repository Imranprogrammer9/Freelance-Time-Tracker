---
name: kit-translate
description: Translate every app string into one or more languages and wire the locales in
---
You are running **`/kit-translate`** for NowKit.

Goal: translate every user-facing string in the app from English into one or
more developer-picked languages, and wire the new locales into the kit so the
in-app language picker offers them.

**Run this in the final phase of development** — once the app's screens and
copy are stable. Re-running it after copy changes will re-translate everything.

Audience: first-time mobile developers / vibe coders. Be brief; you do the
work, they pick the languages.

## Step 1 — Pre-flight

Confirm:
- `app/src/main/res/values/strings.xml` exists and is the source of truth.
- The app compiles (`/kit-compile-app`) before starting. If it does not, stop
  and tell the developer to fix the build first — translations are pointless
  on a broken codebase.
- **Detect already-translated locales** — list the existing
  `app/src/main/res/values-*/strings.xml` dirs. These locales are already done;
  in Step 3 you'll skip them and translate only the new ones (unless the developer
  explicitly asks to re-translate after copy changes).

## Step 2 — Pick language buckets

Ask the user (wait for their answer; multiple choices allowed), with
exactly these four options (use the labels and descriptions below verbatim so
the language list is visible):

- **Label:** `Keep English only`
  **Description:** No translations — the app stays English-only. (If you pick
  this together with any bucket below, the buckets win — uncheck this if you
  actually want translations.)

- **Label:** `Right-to-Left (RTL)`
  **Description:** Arabic (ar), Hebrew (he), Urdu (ur). Adds RTL layout support
  to the app for free as a side effect. (Hindi is NOT RTL — it lives in the
  Asia bucket below.)

- **Label:** `Asia (East / Southeast / South)`
  **Description:** Simplified Chinese (zh-CN), Traditional Chinese (zh-TW),
  Japanese (ja), Korean (ko), Hindi (hi), Thai (th), Vietnamese (vi),
  Indonesian (id), Malay (ms), Filipino (fil).

- **Label:** `Europe`
  **Description:** German (de), Spanish (es), French (fr), Italian (it),
  Portuguese (pt), Dutch (nl), Russian (ru), Polish (pl), Ukrainian (uk),
  Turkish (tr), Czech (cs), Hungarian (hu), Danish (da), Swedish (sv),
  Finnish (fi), Norwegian (nb), Greek (el), Romanian (ro), Slovak (sk),
  Croatian (hr).

### Resolving the picks

- If only **Keep English only** is selected (or nothing at all): tell the
  developer there's nothing to translate, do NOT touch any file, and stop.
- If any other bucket is selected, **ignore Keep English only** — the buckets
  win. Translate every language inside every selected bucket.
- If the developer instead names specific languages (free-text) rather than
  whole buckets, parse their answer against the language tables in Step 3 and
  translate only those.

Confirm the resolved language list back to the developer as a short bulleted
summary before proceeding.

## Step 3 — Translate

**Skip locales already present** (from the Pre-flight scan): if `values-<TAG>/`
already exists for a picked language, tell the developer "`<tag>` already done —
skipping" and don't redo it, unless they asked to re-translate. Translate only the
new locales.

For each picked language, do the following:

1. Read `app/src/main/res/values/strings.xml` in full.
2. Translate every `<string name="...">` value into the target language using
   the native script for that language (e.g. Arabic in Arabic script, Japanese
   in kanji + kana, Chinese in the correct simplified / traditional script).
3. **Preserve** every XML attribute, every escape (`\'`, `\"`, `\n`,
   `&amp;`), every formatting placeholder (`%s`, `%1$s`, `%d`, etc.), and every
   `<string-array>` / `<plurals>` element shape — translate values only,
   never names or structure.
4. **Do NOT translate**:
   - `app_name` (keep the original — it is the brand name).
   - Any string whose value is a URL, an applicationId-like dotted identifier,
     or a config code (e.g. `goog_…`).
   - HTML tags inside string values — translate text, keep tags.
5. Write the translated file to
   `app/src/main/res/values-<TAG>/strings.xml` using the Android resource-dir
   tag from the table below.
6. Sanity-check: the new file must have **exactly** the same number of
   `<string>` / `<string-array>` / `<plurals>` elements as the source.

### Android resource-dir tags (note the `-r` for region)

| Pick | Android dir tag |
|---|---|
| Arabic | `ar` |
| Czech | `cs` |
| Danish | `da` |
| German | `de` |
| Greek | `el` |
| Spanish | `es` |
| Finnish | `fi` |
| Filipino | `fil` |
| French | `fr` |
| Hebrew | `iw` (Android legacy code for he) |
| Hindi | `hi` |
| Croatian | `hr` |
| Hungarian | `hu` |
| Indonesian | `in` (Android legacy code for id) |
| Italian | `it` |
| Japanese | `ja` |
| Korean | `ko` |
| Malay | `ms` |
| Dutch | `nl` |
| Norwegian (Bokmål) | `nb` |
| Polish | `pl` |
| Portuguese | `pt` |
| Romanian | `ro` |
| Russian | `ru` |
| Slovak | `sk` |
| Swedish | `sv` |
| Thai | `th` |
| Turkish | `tr` |
| Ukrainian | `uk` |
| Urdu | `ur` |
| Vietnamese | `vi` |
| Simplified Chinese | `zh-rCN` |
| Traditional Chinese | `zh-rTW` |

### Native display names (for LocaleManager / locales_config)

| Pick | BCP-47 tag | Native display name |
|---|---|---|
| Arabic | `ar` | العربية |
| Czech | `cs` | Čeština |
| Danish | `da` | Dansk |
| German | `de` | Deutsch |
| Greek | `el` | Ελληνικά |
| Spanish | `es` | Español |
| Finnish | `fi` | Suomi |
| Filipino | `fil` | Filipino |
| French | `fr` | Français |
| Hebrew | `he` | עברית |
| Hindi | `hi` | हिन्दी |
| Croatian | `hr` | Hrvatski |
| Hungarian | `hu` | Magyar |
| Indonesian | `id` | Bahasa Indonesia |
| Italian | `it` | Italiano |
| Japanese | `ja` | 日本語 |
| Korean | `ko` | 한국어 |
| Malay | `ms` | Bahasa Melayu |
| Dutch | `nl` | Nederlands |
| Norwegian (Bokmål) | `nb` | Norsk bokmål |
| Polish | `pl` | Polski |
| Portuguese | `pt` | Português |
| Romanian | `ro` | Română |
| Russian | `ru` | Русский |
| Slovak | `sk` | Slovenčina |
| Swedish | `sv` | Svenska |
| Thai | `th` | ไทย |
| Turkish | `tr` | Türkçe |
| Ukrainian | `uk` | Українська |
| Urdu | `ur` | اردو |
| Vietnamese | `vi` | Tiếng Việt |
| Simplified Chinese | `zh-CN` | 简体中文 |
| Traditional Chinese | `zh-TW` | 繁體中文 |

## Step 4 — Wire the locales in

For every picked language, update three places. Existing entries stay; you
only add.

1. **`app/src/main/res/xml/locales_config.xml`** — append a `<locale>` per
   picked language using the **BCP-47 tag** (column 2 of the table above —
   `he` not `iw`, `id` not `in`).

2. **`app/build.gradle.kts`** — extend `localeFilters` to include every
   picked tag (BCP-47 form). E.g.:

       localeFilters += setOf("en", "es", "ja", "zh-CN")

3. **`LocaleManager.kt`** — append a `Language(tag = "<bcp47>", displayName =
   "<native>")` entry to `supported` for each pick. Use the native display
   name from the table.

Hebrew / Indonesian note: the **resource directory** uses the Android legacy
codes (`values-iw`, `values-in`), but `locales_config.xml`, `localeFilters`,
and `LocaleManager.supported` use the modern BCP-47 codes (`he`, `id`).
Android maps both automatically.

## Step 5 — Verify

Run `./gradlew :app:compileDebugKotlin` to make sure the wiring compiles. If
it fails, the usual cause is a malformed `strings.xml` — fix the broken file
and re-run.

Then report back:
- Number of strings translated per language.
- The new entries in `locales_config.xml`, `localeFilters`, and
  `LocaleManager.supported`.
- A reminder that Settings → Language now shows the picker (it auto-hides
  when only English is wired).

That is the whole job. Do not touch screenshots, listing copy, or anything in
`playstore/` — translating store listing copy is a separate concern and is
handled by `/kit-upload-on-google-play`.
