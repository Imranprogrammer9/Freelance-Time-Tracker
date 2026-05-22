---
description: Configure remote config, the update gate, and push notifications
---

You are running **`/setup-ops`** for ShipKit. Goal: wire the "operations"
features — remote config / feature flags, the force/soft update gate,
maintenance mode, and FCM push notifications.

Audience: first-time mobile developers. Be brief; you make the edits.

**Docs:** https://kit.shipkaro.dev/docs/ops

When a section below shows a block quoted with `>`, present that block to the
developer **verbatim** — do not paraphrase or improvise. Prose outside those
blocks is instructions for you, not the developer.

## Step 1 — Remote config provider

The update gate, maintenance mode, and the changelog all read their values from
`RemoteAppConfig`. Ask (AskUserQuestion) — present these options exactly:
- **Local (recommended to start)** — returns defaults, no backend. The update
  gate and maintenance screen exist but never trigger. The app builds offline.
- **Firebase Remote Config** — values controlled live from the Firebase console.
  Needs Firebase set up (`google-services.json` + plugins). If the developer has
  not set up Firebase yet — for example they chose Supabase for auth — this step
  sets it up.

Set `REMOTE_CONFIG_PROVIDER` in `KitConfig.kt` accordingly.

If they pick **Firebase Remote Config**, first check whether Firebase is already
configured: does `app/google-services.json` exist? If it does NOT, read
`.claude/commands/setup-firebase.md`, follow it to set Firebase up, then return
here. If it already exists, do not repeat that — just continue.

If they picked Firebase, show the developer exactly this:

> **Remote Config keys ShipKit reads** — create these in the Firebase console
> under Remote Config, or leave any unset and the kit's defaults apply:
> - `min_supported_version` — below this version code, force an update.
> - `latest_version` — newest version code available.
> - `latest_version_name` — newest version name, shown to users.
> - `maintenance_mode` — `true` shows the maintenance screen.
> - `maintenance_message` — text shown on the maintenance screen.
> - `app_changelog` — JSON string powering the in-app "What's new" screen.

## Step 2 — Push notifications (FCM)

Ask whether the app needs push notifications.
- **No** — nothing to do. The `POST_NOTIFICATIONS` permission and
  `KitMessagingService` declared in the manifest are harmless when unused.
- **Yes** — FCM needs Firebase. Read `.claude/commands/setup-firebase.md` and
  follow it. The kit already declares `KitMessagingService` and a notification
  channel in `AndroidManifest.xml`, so no further code wiring is needed.

## Step 3 — Deep links / App Links

The kit ships an App Links intent-filter in `AndroidManifest.xml` with host
`shipkaro.dev` (autoVerify). Ask the developer for their own domain and replace
that host. Then show them exactly this:

> **Verify your App Links:**
> Android treats your links as verified only if your domain serves a file at
> `https://YOUR-DOMAIN/.well-known/assetlinks.json` listing your app's package
> name and signing-certificate SHA-256 fingerprint. See Android's "Verify
> Android App Links" guide for the exact file contents.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was configured and what was left at defaults.
