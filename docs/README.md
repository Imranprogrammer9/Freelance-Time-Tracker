# NowKit Documentation

The production-ready Android starter kit that your AI agent drives from the terminal.
Auth, payments, analytics, onboarding, a 40+ component design system, and a one-command
path to Google Play — already wired. You answer questions; the kit writes the code.

> **New here?** Read **[Introduction](#introduction)**, then jump straight to
> **[The Flow — 0 to Google Play](the-flow.md)**. That single page is the whole journey.

---

## How these docs are organised

| Section | What's in it |
|---------|--------------|
| **[Getting Started](getting-started.md)** | Requirements, machine setup (`/kit-env-check`), cloning, first run, project structure |
| **[The Flow — 0 to Google Play](the-flow.md)** | The complete journey: idea → configured app → live on the Play Store, every command in order |
| **[Commands Reference](commands.md)** | Every `/kit-*` command explained — setup, design, release, utilities |
| **[Example Recipes](example-recipes.md)** | Five real apps (free, subscription, offline, AI) showing exactly which choices to make |
| **[Components Catalog](components.md)** | The 40+ UI components, state views, settings rows, permission flows |
| **[Features Reference](features.md)** | How auth, paywall, analytics, AI, remote config, and security work under the hood |

---

## Introduction

NowKit is a **pure Android native** starter kit — Kotlin + Jetpack Compose + Material 3.
It is **not** a framework you import; it's a real app you **own**. You clone it, rename it,
turn on the features you need, build your screens, and ship.

What makes it different from every other starter kit:

- **🤖 AI-agent ready.** Every setup step is a slash command (`/kit-start-setup`,
  `/kit-setup-auth`, …). Your AI coding agent — Claude Code, Cursor, or Windsurf — runs
  them, asks you plain questions, and edits the code for you. You never copy-paste keys
  into the wrong file.
- **🏁 End-to-end.** Design → onboarding → auth → paywall → analytics → permissions →
  privacy policy → Play Store upload. All of it is in the kit, all of it driven by commands.
- **💻 No Android Studio needed.** Everything runs from the terminal. Android Studio is
  only used once, to install the SDK.

You don't need to be an Android developer. If you can run a command and answer a question,
you can ship an app with NowKit.

### What you get out of the box

- 🔑 **Authentication** — email + Google sign-in (Supabase or Firebase)
- 💳 **Paywall + subscriptions** — RevenueCat
- 📊 **Analytics + crash reporting** — PostHog, Firebase Analytics, Crashlytics, Sentry
- 🤖 **AI features** — OpenRouter (one key, 100+ models)
- 🎨 **Design system** — 40+ Compose components in your brand color + icon pack
- 🧭 **Onboarding** — a simple 3-page intro, or a personalised Calm/Headspace-style quiz
- 🛡️ **Permission flows** — pretty "allow camera/photos/notifications" screens, prebuilt
- 🌐 **Localization** — English by default; add any language with one command
- ⚙️ **Ops** — remote config, force/soft update gate, maintenance mode, in-app changelog
- 🚀 **Release** — Play Store assets, listing copy, privacy policy, Data Safety, upload

### The mental model

There are two kinds of "config" in the kit, kept strictly separate:

- **`KitConfig`** — compile-time switches **you** set once (which auth provider, paywall
  hard/soft, which analytics). The setup commands edit this for you.
- **`RemoteAppConfig`** — runtime values you change later **without shipping an update**
  (force-update version, maintenance mode, changelog). Backed by Supabase, Firebase, or local.

You rarely touch either by hand. The commands do it. This doc set explains what they do so
you understand your own app.

---

## Where to go next

- **Just got the kit?** → [Getting Started](getting-started.md)
- **Want the big picture?** → [The Flow — 0 to Google Play](the-flow.md)
- **Building something specific?** → [Example Recipes](example-recipes.md)
- **Looking up one command?** → [Commands Reference](commands.md)

Questions? The community is on WhatsApp (link in your purchase email). The kit's home,
changelog, and updates live at **https://kit.shipkaro.dev**.
