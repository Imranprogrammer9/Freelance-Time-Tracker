---
description: Configure remote config, the update gate, and push notifications
---

You are running **`/setup-ops`** for the ShipKaro Android Kit. Goal: wire the
"operations" features — remote config / feature flags, the force/soft update
gate, maintenance mode, and FCM push notifications.

Audience: first-time mobile developers. Be brief; you make the edits.

## Step 1 — Remote config provider

The update gate, maintenance mode, and the changelog all read their values from
`RemoteAppConfig`. Ask (AskUserQuestion):
- **Local (recommended to start)** — returns defaults, no backend. The update
  gate and maintenance screen exist but never trigger. The app builds offline.
- **Firebase Remote Config** — values controlled live from the Firebase console.

Set `REMOTE_CONFIG_PROVIDER` in `KitConfig.kt` accordingly. If they pick
Firebase, read `.claude/commands/setup-firebase.md`, follow it, then return here.

If they picked Firebase, tell them the keys the kit reads from Remote Config so
they can create them in the console: `min_supported_version`, `latest_version`,
`latest_version_name`, `maintenance_mode`, `maintenance_message`, and
`app_changelog` (a JSON string). They can leave any unset — defaults apply.

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
that host. Tell them verified App Links also need a
`/.well-known/assetlinks.json` file hosted on that domain — point them to
Android's "Verify Android App Links" docs for the file contents.

## Step 4 — Verify

**Skip this step if you are running as part of `/start-kit`** — it builds once at
the end. If this command was run on its own, run
`./gradlew :app:compileDebugKotlin`.

Report what was configured and what was left at defaults.
