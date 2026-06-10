---
description: Verify your machine has every tool the kit needs (JDK, Android SDK, android CLI, skills, optional tools)
---

You are running **`/kit-env-check`** for ShipKit. Goal: detect which tools the
ShipKaro mobile dev environment expects, report what's installed vs missing,
and print **OS-specific install commands** for anything missing.

Audience: first-time mobile developers / vibe coders. Be brief; print results
in a single table; show install commands only for the tools the dev is missing.

**Reference setup pages (one per OS):**
- macOS:  https://www.shipkaro.dev/mobile-docs/getting-started/setup-macos
- Windows: https://www.shipkaro.dev/mobile-docs/getting-started/setup-windows

This command is **per-machine** — run it once after a fresh OS install or when
you switch to a new laptop. It does NOT duplicate `/kit-start-setup` Step 0
(which covers per-project setup: `local.properties` + Android SDK path +
Gradle wrapper).

## Step 1 — Detect the OS

Run `uname` and branch:

- `Darwin*` → **macOS**
- `Linux*` → **Linux**
- `MINGW*` / `MSYS*` / `CYGWIN*` → **Windows** (under Git Bash / WSL)

If `uname` errors or returns something unexpected, ask the developer which OS
they're on. Record the result — every install command in Step 3 branches on
this.

## Step 2 — Probe every tool

Run each probe and record pass / fail. Use parallel Bash calls where the
commands are independent.

| Tool | Probe command | Pass condition |
|---|---|---|
| JDK 17 | `java -version 2>&1 \| head -1` | output contains `"17."` or `version "17`  |
| ANDROID_HOME | `echo "$ANDROID_HOME"` | non-empty AND the path exists (`test -d "$ANDROID_HOME"`) |
| adb | `adb version 2>&1 \| head -1` | output starts with `"Android Debug Bridge"` |
| android CLI | `android --version 2>&1 \| head -1` | exits 0 with a version line |
| Android Skills | `android skills list 2>&1 \| wc -l` | result > 1 |
| scrcpy (optional) | `scrcpy --version 2>&1 \| head -1` | exits 0 with a version line |
| Holo (optional) | `command -v holo` | prints a path (Holo is on PATH) — Holo has no `--version` flag, so only check that it is installed, never run `holo --version` |
| gh / GitHub CLI (optional) | `gh --version 2>&1 \| head -1` | output starts with `"gh version"` — needed only for `/kit-save-to-github` (backing your app up to GitHub) |

For `ANDROID_HOME` on Windows under Git Bash, also accept `%LOCALAPPDATA%\Android\Sdk`
expansion — if the env var is empty, check whether the default path exists:
`test -d "$LOCALAPPDATA/Android/Sdk"` (or wherever Android Studio installed it).

## Step 3 — Print the report

Print this verbatim (with the actual pass/fail per row):

> **Environment check — <OS detected>**
>
> ```
> ✓ / ✗  JDK 17
> ✓ / ✗  ANDROID_HOME
> ✓ / ✗  adb
> ✓ / ✗  android CLI
> ✓ / ✗  Android Skills
> ○      scrcpy (optional)
> ○      Holo (optional)
> ○      gh / GitHub CLI (optional)
> ```
>
> Legend: ✓ = installed · ✗ = missing · ○ = optional, not installed.

For each **missing required** tool (✗), print the install block for the
detected OS. For each **missing optional** tool (○), ask the developer
whether they want to install it now (don't push).

## Step 4 — Install commands per OS

For each missing tool, print only the block for the developer's OS.

### JDK 17

**macOS:**

    brew install openjdk@17
    sudo ln -sfn $(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk \
        /Library/Java/JavaVirtualMachines/openjdk-17.jdk

**Windows (PowerShell, Scoop):**

    scoop bucket add java
    scoop install openjdk17

**Linux (Ubuntu / Debian):**

    sudo apt update && sudo apt install -y openjdk-17-jdk

Verify with `java -version`.

### ANDROID_HOME

Install Android Studio first (https://developer.android.com/studio) — it
ships the SDK. Then set the env var:

**macOS / Linux (zsh / bash):** add to `~/.zshrc` or `~/.bashrc`:

    export ANDROID_HOME="$HOME/Library/Android/sdk"   # macOS
    export ANDROID_HOME="$HOME/Android/Sdk"           # Linux
    export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin"

Open a new terminal so the change takes effect.

**Windows (PowerShell):**

    [Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "User")
    [Environment]::SetEnvironmentVariable("Path", "$env:Path;$env:LOCALAPPDATA\Android\Sdk\platform-tools;$env:LOCALAPPDATA\Android\Sdk\cmdline-tools\latest\bin", "User")

Open a new PowerShell so the change takes effect.

### adb

Comes with `platform-tools` inside the Android SDK. If `adb version` fails,
either `ANDROID_HOME` is wrong or `platform-tools` isn't on PATH. Re-run the
ANDROID_HOME setup above with the `platform-tools` path included in PATH.

### android CLI

Tells Claude Code how to do Android-specific things (project creation, deploy,
SDK manage). Download from https://developer.android.com/tools/agents.

**macOS / Linux:** download the binary, move to `/usr/local/bin/`:

    sudo mv android /usr/local/bin/android
    sudo chmod +x /usr/local/bin/android

**Windows:** put the `android.exe` somewhere on PATH (e.g. `C:\Tools\android\`)
and add that folder to your User PATH env var (System Properties → Environment
Variables → Path → Edit → New).

Then verify:

    android --version

### Android Skills

Once `android` CLI is on PATH, install every skill in one go:

    android skills add --all
    android skills list

Same command on every OS.

### scrcpy (optional)

Mirror your phone's screen on your laptop. Great for demos / pair-coding.

**macOS:**

    brew install scrcpy

**Windows (Scoop):**

    scoop install scrcpy

**Linux:**

    sudo apt install scrcpy   # Ubuntu/Debian
    sudo dnf install scrcpy   # Fedora

Plug in your phone via USB (with USB debugging enabled) and run `scrcpy`.

### Holo (optional)

Terminal-based Android profiler + log browser + DB query + trace recorder.
Replaces clicking around Android Studio for most debug work.

**macOS / Linux:**

    curl -sSL https://raw.githubusercontent.com/measure-sh/holo/main/install.sh | sh

**Windows (any OS with Rust):**

    cargo install holo

(Rust install: https://rustup.rs)

Verify it installed with `command -v holo` (Holo has no `--version` flag). Plug a device in and run `holo` for the TUI.

### gh / GitHub CLI (optional)

Only needed if you want to back your app up to GitHub via `/kit-save-to-github`.
It creates your private repo and pushes for you.

**macOS:**

    brew install gh

**Windows (Scoop):**

    scoop install gh

**Linux:**

    sudo apt install gh   # or see https://github.com/cli/cli#installation

After installing, run `gh auth login` once (browser sign-in). Verify with
`gh --version`.

## Step 5 — Wrap up

After the developer installs the missing pieces, tell them:

> Open a **new terminal** so PATH changes take effect, then re-run
> `/kit-env-check` to confirm everything passes.

If everything ✓, point them at the next step:

> Env is ready. Next:
>  - Brand-new clone? run `/kit-start-setup` to configure the kit.
>  - Already configured? run `/kit-run-app` to install + launch on a device.
