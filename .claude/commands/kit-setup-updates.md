---
description: Configure remote config, the update gate, and push notifications
---

You are running **`/kit-setup-updates`** for ShipKit. Goal: wire the "operations"
features — remote config / feature flags, the force/soft update gate, maintenance
mode, and FCM push notifications.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/ops

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Remote config provider

The update gate, maintenance mode, and the changelog all read their values from
`RemoteAppConfig`. Ask (AskUserQuestion) — present these options exactly:
- **Local (recommended to start)** — returns defaults, no backend. The update
  gate and maintenance screen exist but never trigger. The app builds offline.
- **Supabase** — reads a public `app_config` Postgres table on Supabase. Best
  fit if the app already uses Supabase for auth — reuses `supabase.url` /
  `supabase.key` from `local.properties`, no extra credentials needed.
- **Firebase Remote Config** — values controlled live from the Firebase console.
  Needs Firebase set up (`google-services.json` + plugins). If the developer has
  not set up Firebase yet, this step sets it up.

Set `REMOTE_CONFIG_PROVIDER` in `KitConfig.kt` accordingly.

### Supabase setup

If they picked **Supabase**, show the developer exactly this:

> **Create the `app_config` table in Supabase:**
> 1. In the Supabase dashboard: **Table Editor → New table**, name it
>    `app_config`.
> 2. Set the SELECT policy to `using (true)` so the table is publicly readable
>    (the kit reads it without authenticating). RLS can stay ON.
> 3. Add columns:
>    - `id` (uuid, default `gen_random_uuid()`)
>    - `min_supported_version` (int4)
>    - `latest_version` (int4)
>    - `latest_version_name` (text)
>    - `maintenance_mode` (bool, default `false`)
>    - `maintenance_message` (text)
>    - `app_changelog` (jsonb)
>    - `updated_at` (timestamptz, default `now()`)
> 4. Insert one row with your values. The kit reads the row with the most
>    recent `updated_at` — to ship an update later, insert a new row instead
>    of editing the old one.

### Firebase setup

If they picked **Firebase Remote Config**, first check whether Firebase is
already configured: does `app/google-services.json` exist? If it does NOT, read
`.claude/commands/kit-kit-setup-firebase.md`, follow it to set Firebase up, then return
here. If it already exists, do not repeat that — just continue.

Then show the developer exactly this:

> **Remote Config keys ShipKit reads** — create these in the Firebase console
> under Remote Config, or leave any unset and the kit's defaults apply:
> - `min_supported_version` — below this version code, force an update.
> - `latest_version` — newest version code available.
> - `latest_version_name` — newest version name, shown to users.
> - `maintenance_mode` — `true` shows the maintenance screen.
> - `maintenance_message` — text shown on the maintenance screen.
> - `app_changelog` — JSON string powering the in-app "What's new" screen.

## Push notifications (FCM)

Ask whether the app needs push notifications.
- **No** — nothing to do. The `POST_NOTIFICATIONS` permission and
  `KitMessagingService` declared in the manifest are harmless when unused.
- **Yes** — FCM needs Firebase. Read `.claude/commands/kit-kit-setup-firebase.md` and
  follow it. The kit already declares `KitMessagingService` and a notification
  channel in `AndroidManifest.xml`, so no further code wiring is needed.

## Verify

**Skip this step if you are running as part of `/kit-start-setup`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was configured and what was left at defaults.
