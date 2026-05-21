# Play Data Safety — mapping for the ShipKaro Android Kit

Play Console → App content → Data safety asks how your app collects, shares, and
handles user data. This is the mapping for the kit's default SDKs — adjust for any
module you disabled in `KitConfig` and for the data **your own** code collects.

> This is guidance, not legal advice. You are responsible for the accuracy of the
> declaration. Re-check it whenever you add an SDK.

## Per-SDK data collection

| SDK | Data type (Play category) | Collected | Shared | Purpose |
|---|---|---|---|---|
| Supabase Auth | Email address | Yes | No | Account management |
| Google Sign-In | Name, email, photo | Yes | No | Account management |
| RevenueCat | Purchase history | Yes | No | App functionality |
| PostHog | App interactions, device IDs | Yes | No* | Analytics |
| Firebase Analytics | App interactions, device IDs | Yes | No* | Analytics |
| Firebase Crashlytics | Crash logs, diagnostics | Yes | No* | Crash diagnostics |

\* "Shared" in Play's sense means transfer to a *separate company*. Sending data to
your own analytics processor is collection, not sharing — but confirm against each
provider's current data-processing terms.

## Form answers (typical kit defaults)

- **Does your app collect or share data?** Yes
- **Is data encrypted in transit?** Yes (all SDKs use HTTPS/TLS)
- **Can users request data deletion?** Yes — Settings → Account → Delete account
  removes the auth account and wipes local Room + DataStore data.
- **Is any collected data required?** Email is required only if auth is enabled
  (`KitConfig.AUTH_ENABLED`). Analytics is optional — users opt out in
  Settings → Privacy.

## Account deletion URL

Play also requires a web URL describing account deletion (App content → Data
deletion). Point it at a section of your hosted privacy policy that explains the
in-app Delete account flow.

## When you change modules

- Disabled auth → remove the Supabase / Google rows.
- Disabled analytics → remove PostHog / Firebase Analytics rows (Crashlytics may
  stay if you keep crash reporting).
- Added an SDK → add a row and re-review the form.
