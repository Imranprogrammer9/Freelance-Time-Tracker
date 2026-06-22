# NowKit

Production-ready, **pure Android-native** starter kit (Kotlin + Jetpack Compose) that your
AI agent drives from the terminal. Auth, payments, analytics, onboarding, a 40+ component
design system, and a one-command path to Google Play — already wired. You answer questions;
the kit writes the code.

> **You own this — it isn't a dependency you import.** You start from a copy of this repo
> and that copy *becomes your app*.

## Quick start

1. **Clone the kit** into a folder named for your app — the clone *becomes* your app
   (you don't have your own repo yet; you make one later with `/kit-save-to-github`):

   ```bash
   git clone https://github.com/wajahatkarim3/shipkaro-android-kit my-app
   cd my-app
   ```

   (`my-app` = whatever you want the project folder called.)

2. **Open the folder in your AI agent** — Claude Code: run `claude` in that directory
   (also works with Cursor, Google Antigravity, OpenCode, Codex). The `/kit-*` commands
   ship **inside** the repo (`.claude/commands/`, `.cursor/commands/`, …), so they're
   available the moment you open it — nothing to install.

3. **Run the setup command.** It renames the kit to your app, wires auth / paywall /
   analytics, and builds:

   ```
   /kit-start-setup
   ```

Type `/kit-` any time to see every command.

## 📚 Full docs — the central, always-current source

Everything lives at **https://kit.shipkaro.dev/docs**:

- [Getting Started](https://kit.shipkaro.dev/docs/creating-new-project)
- [Commands Reference](https://kit.shipkaro.dev/docs/commands)
- [The Flow — 0 to Google Play](https://kit.shipkaro.dev/docs/the-flow)
- [Changelog](https://kit.shipkaro.dev/changelog)

Agent guide (read by Claude Code / Cursor / Antigravity / OpenCode / Codex):
[AGENTS.md](AGENTS.md).
