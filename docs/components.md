# Components Catalog

NowKit ships a complete **Jetpack Compose + Material 3** design system — 40+ components, all
themed by your **brand color** and **icon pack**. They live in
`core/designsystem/` and every component is prefixed `Kit` (`KitButton`, `KitTextField`, …).

> **See them live.** On your Home screen, tap **Browse kit components** to open the in-app
> **Components Catalog** screen — every component rendered in *your* colors, with real
> previews. It's the fastest way to see what's available. (Delete it in three places when
> you're done exploring: the screen file, its `Route`, and the Home button.)

You consume components through the theme — e.g. icons via `KitTheme.icons.back`, colors via
`KitTheme.colors`, spacing via `KitTheme.spacing`. Every component that takes an icon also
accepts a per-call-site override.

---

## Foundation

The design tokens everything is built on (`core/designsystem/foundation/` + `theme/`):

- **Spacing / Shape / Elevation** tokens
- **Color scheme** — derived entirely from your one brand color (light + dark + containers)
- **Typography** — full Material 3 type scale
- **Icons** — pluggable packs: `MaterialKitIcons` (default), `FeatherKitIcons`,
  `TablerKitIcons`, swappable kit-wide via `LocalKitIcons`
- **Edge-to-edge** + light/dark/system theming

---

## Buttons & inputs

| Component | What it is |
|-----------|------------|
| `KitButton` | Primary / secondary / text / loading variants |
| `KitTextField` | Text input with label, error, helper |
| `KitPasswordField` | Password input with show/hide |
| `KitSearchField` | Search box with magnifier + clear |
| `KitOtpField` | N-cell one-time-code entry |
| `KitSlider` | Value slider |
| `KitStepper` | +/− number stepper |
| `KitSegmentedButton` | Single-choice segmented row |
| `KitDropdownMenu` | Generic exposed dropdown `<T>` |
| `KitDatePicker` | Modal + inline date pickers |

## Containers & lists

| Component | What it is |
|-----------|------------|
| `KitCard` | Plain + clickable card |
| `KitListItem` | Standard list row |
| `KitDisclosureGroup` | Expandable group |
| `KitSwipeableRow` | Swipe-to-action row |
| `KitLazyGrid` | Fixed + adaptive grids |
| `KitBottomSheet` | Modal bottom sheet |
| `KitDialog` | Plain + destructive dialogs |

## Feedback & status

| Component | What it is |
|-----------|------------|
| `KitSnackbar` | 4 severity styles — `.info()` `.success()` `.warning()` `.error()` |
| `KitBanner` | Inline banner, 4 styles, dismissible |
| `KitLinearProgress` | Determinate + indeterminate progress |
| `KitShimmer` / `KitSkeleton` | Loading placeholders |
| `KitPullToRefresh` | Pull-to-refresh wrapper |
| `KitBadge` | Number / text / dot overlay |
| `KitRatingBar` | Tappable 5-star rating |

## State views

Full-screen or inline (`core/designsystem/state/`):

- `KitLoadingState` / `KitInlineLoading`
- `KitEmptyState`
- `KitErrorState`
- `KitSuccessState`

## Navigation & scaffolds

- `KitBottomNavScaffold` + `KitBottomNavBar` + `KitBottomNavItem` (with badges)
- `KitFab` — single + extended floating action button

## Settings rows

Compose your Settings screen from (`core/designsystem/settings/`):

- `SettingsSection`, `SettingsDivider`
- `ToggleRow`, `NavRow`, `AccountRow`, `DangerRow`
- `LegalLinks`

## Onboarding

- `KitOnboardingPager` + `KitPageIndicator` — the 3-page intro
- (Personalised questionnaire onboarding via `/kit-design-onboarding`)

## Pricing / paywall pieces

- `KitPricingCard` — tier card
- `KitFeatureRow`, `KitBulletRow` — feature lists

## Ops components

For the update gate + changelog (`core/designsystem/ops/`):

- `KitUpdateSheet` — force / soft update prompts
- `KitWhatsNewSheet` — version-bump changelog popup
- `KitMaintenanceScreen` — full-screen maintenance mode

## Permission flows

Two layers (`core/designsystem/permission/` + `feature/permissions/`):

- `KitPermissionPrimer` / `KitPermissionPrimerSheet` — bottom-sheet rationale
- `KitPermissionScreen` — full-screen flow (icon → rationale → Allow / Skip; auto-swaps to
  "Open Settings" when the OS permanently denies)
- **8 prebuilt screens**: Notifications, Camera, Microphone, Photo, Contacts, Calendar,
  Location, Motion

## WebView

- `KitWebView` — embedded web view (JS off by default; prefer Custom Tabs for external links)

---

## Utilities

Helper classes in `core/util/` and elsewhere:

- `CustomTabs` — open URLs in Chrome Custom Tabs
- `PlayStoreLauncher` — open your Play listing (the "Rate" button)
- `EmailLauncher` — `mailto:` with subject/body
- `KitHaptics` — semantic haptics (`lightTap`, `success`, …)
- `SecureDataStore` — encrypted key storage (AES-GCM + Android Keystore)

---

**Next:** [Features Reference](features.md) — how auth, paywall, analytics, AI, and ops work
underneath these components.
