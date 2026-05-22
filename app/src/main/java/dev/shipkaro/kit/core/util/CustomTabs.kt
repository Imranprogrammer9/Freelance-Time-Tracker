package dev.shipkaro.kit.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Open a URL in Chrome Custom Tabs. If no browser supporting Custom Tabs is installed,
 * `CustomTabsIntent` automatically falls back to a regular `ACTION_VIEW` intent.
 *
 * No-ops on blank URLs (so [KitConfig.PRIVACY_URL] / `TERMS_URL` placeholders cleared
 * to empty string just disable the link).
 */
object CustomTabs {
    fun openUrl(context: Context, url: String) {
        if (url.isBlank()) return
        runCatching {
            CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
        }.onFailure { Log.w("CustomTabs", "Failed to open $url", it) }
    }
}
