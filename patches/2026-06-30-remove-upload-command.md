# Remove the legacy `/kit-upload-on-google-play` command

- **Date:** 2026-06-30
- **Applies when:** a `kit-upload-on-google-play*` command file still exists in your agent's
  command/skill dir (i.e. you set the app up before 2026-06-30).
- **Adds dependency:** no
- **Why:** `/kit-upload-on-google-play` was consolidated into `/kit-publish-to-play` (which does
  everything it did, in the right order). `/kit-update` refreshes command files but **does not
  delete** ones removed upstream, so the stale command lingers in your repo and shows up in
  `/kit-` — delete it so there's only one publish command.

## Edits

### 1. Delete the old command from your agent's command/skill dir(s)

Only your own agent's directory applies — remove whichever of these exist (the `*` covers the
`-part-2` / `-part-3` continuation files some agents split it into):

```bash
git rm -r --ignore-unmatch \
  .claude/commands/kit-upload-on-google-play.md \
  .cursor/commands/kit-upload-on-google-play*.md \
  .opencode/commands/kit-upload-on-google-play*.md \
  .agent/workflows/kit-upload-on-google-play*.md \
  .codex/skills/kit-upload-on-google-play*
```

If the files aren't tracked by git, use plain `rm -rf` on the same paths instead. This is
idempotent — if nothing matches (already deleted), it's a no-op.

## Verify

Typing `/kit-` no longer lists `/kit-upload-on-google-play`; `/kit-publish-to-play` is the only
publish-to-Play command.
