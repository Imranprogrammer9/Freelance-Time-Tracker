package com.freelance.timetracker.core.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Timber [Timber.Tree] that mirrors WARN+ logs into Firebase Crashlytics:
 *  - `WARN` and `INFO` go to `Crashlytics.log()` so they appear as breadcrumbs on a future crash.
 *  - `ERROR` / `ASSERT` with a non-null `Throwable` are reported as non-fatal via `recordException()`.
 *  - `ERROR` / `ASSERT` with no throwable also `recordException(Throwable(message))` so the
 *    error reaches the Crashlytics dashboard.
 *  - `DEBUG` / `VERBOSE` are dropped — they're for local dev only.
 *
 * Planted in release builds from [KitApplication]. Inert when Firebase Crashlytics is not
 * configured (no `google-services.json`): `FirebaseCrashlytics.getInstance()` either no-ops
 * or throws, both of which we swallow.
 */
class CrashlyticsTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            val tagged = if (tag.isNullOrBlank()) message else "[$tag] $message"
            crashlytics.log(tagged)
            if (priority >= Log.ERROR) {
                crashlytics.recordException(t ?: Throwable(tagged))
            }
        }
    }
}
