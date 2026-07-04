package com.freelance.timetracker.core.ops

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.freelance.timetracker.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Google Play In-App Review. Ported from the ShipKaro KMM starter.
 *
 * The Play UI only actually appears for apps installed from the Play Store, and Google
 * quota-limits how often it shows — so [requestReview] is best-effort and never errors.
 * Debug builds use [FakeReviewManager] (logs, shows nothing) to avoid confusing no-ops.
 */
class InAppReviewManager {

    suspend fun requestReview(activity: Activity) {
        val manager = if (BuildConfig.DEBUG) {
            FakeReviewManager(activity)
        } else {
            ReviewManagerFactory.create(activity)
        }
        runCatching {
            suspendCancellableCoroutine { cont ->
                manager.requestReviewFlow().addOnCompleteListener { request ->
                    if (request.isSuccessful) {
                        manager.launchReviewFlow(activity, request.result)
                            .addOnCompleteListener { cont.resume(Unit) }
                    } else {
                        cont.resume(Unit)
                    }
                }
            }
        }
    }
}
