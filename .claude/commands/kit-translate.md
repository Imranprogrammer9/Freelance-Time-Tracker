---
description: Translate every app string into one or more languages and wire the locales in
---

You are running **`/kit-translate`** for ShipKit.

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

## Step 2 — Pick languages

Show the developer the full supported list, verbatim and numbered:

> **Pick the languages you want to translate the app into.** Reply with the
> numbers (e.g. `1, 7, 16`) or the names — whichever is easier.
>
> ```
>  1. Arabic               (ar)
>  2. Czech                (cs)
>  3. Danish               (da)
>  4. German               (de)
>  5. Greek                (el)
>  6. Spanish              (es)
>  7. Finnish              (fi)
>  8. Filipino             (fil)
>  9. French               (fr)
> 10. Hebrew               (he)
> 11. Croatian             (hr)
> 12. Hungarian            (hu)
> 13. Indonesian           (id)
> 14. Italian              (it)
> 15. Japanese             (ja)
> 16. Korean               (ko)
> 17. Malay                (ms)
> 18. Dutch                (nl)
> 19. Norwegian (Bokmål)   (nb)
> 20. Polish               (pl)
> 21. Portuguese           (pt)
> 22. Romanian             (ro)
> 23. Russian              (ru)
> 24. Slovak               (sk)
> 25. Swedish              (sv)
> 26. Thai                 (th)
> 27. Turkish              (tr)
> 28. Ukrainian            (uk)
> 29. Vietnamese           (vi)
> 30. Simplified Chinese   (zh-CN)
> 31. Traditional Chinese  (zh-TW)
> ```

Wait for the reply, parse the picks, and confirm back as a short list before
proceeding. (English is always the base — never asked.)

Note: AskUserQuestion is intentionally NOT used here — its 4-option limit
cannot show 30+ languages in one prompt.

## Step 3 — Translate

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
