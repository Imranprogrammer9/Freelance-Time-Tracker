package com.freelance.timetracker.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

/**
 * Opens this app's Google Play listing — used by Settings → Rate to send users to the
 * Play Store page (and its built-in 5-star rating UI) instead of the in-app review prompt.
 *
 * Why this and not [InAppReviewManager]?
 *  - Play's in-app review dialog is quota-throttled by Google; calling it from a settings
 *    row "Rate" button silently no-ops most of the time and confuses users.
 *  - The in-app review API also doesn't expose the rating, so devs can't react to it.
 *  - Sending users to the Play listing always works, always shows the full review UI, and
 *    lets the user see other reviews + screenshots.
 *
 * Devs who want a smarter trigger (e.g. show the Play review prompt after N launches) can
 * run `/kit-setup-review-dialog` to wire [InAppReviewManager.requestReview] at a chosen
 * call-site. The Settings row stays on the Play listing path.
 */
object PlayStoreLauncher {
    fun openListing(context: Context, packageName: String = context.packageName) {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        runCatching { context.startActivity(market) }
            .recoverCatching { context.startActivity(web) }
            .onFailure { Timber.tag("PlayStore").w(it, "Failed to open Play listing for %s", packageName) }
    }
}
