package dev.shipkaro.kit.core.log

import android.util.Log
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel
import timber.log.Timber

/**
 * Timber [Timber.Tree] that mirrors INFO+ logs into Sentry — runs alongside
 * [CrashlyticsTree] (Timber supports any number of trees, each fires
 * independently).
 *
 *  - `INFO` / `WARN` are added as breadcrumbs (so they appear on a future crash).
 *  - `ERROR` / `ASSERT` with a non-null `Throwable` are captured via
 *    `Sentry.captureException()`.
 *  - `ERROR` / `ASSERT` without a throwable are captured via
 *    `Sentry.captureMessage()`.
 *  - `DEBUG` / `VERBOSE` are dropped.
 *
 * Planted by [KitApplication] only when [KitConfig.SENTRY_ENABLED] is true AND
 * `BuildConfig.SENTRY_DSN` is non-blank. Inert otherwise.
 */
class SentryTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        runCatching {
            val tagged = if (tag.isNullOrBlank()) message else "[$tag] $message"
            if (priority >= Log.ERROR) {
                if (t != null) {
                    Sentry.captureException(t)
                } else {
                    Sentry.captureMessage(tagged, SentryLevel.ERROR)
                }
            } else {
                val breadcrumb = Breadcrumb().apply {
                    this.message = tagged
                    level = if (priority == Log.WARN) SentryLevel.WARNING else SentryLevel.INFO
                }
                Sentry.addBreadcrumb(breadcrumb)
            }
        }
    }
}
