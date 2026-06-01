package dev.shipkaro.kit.core.designsystem.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * In-app web view. Use this when the content **must** render inside your app
 * (paid-content viewer, embedded help page). For external links (privacy policy,
 * terms, marketing URLs) use [dev.shipkaro.kit.core.util.CustomTabs] instead —
 * Chrome Custom Tabs share the user's cookies, autofill, and password manager.
 *
 * JavaScript is disabled by default. Enable explicitly via [javaScriptEnabled]
 * only when the destination requires it; never load arbitrary user-supplied
 * URLs with JS on.
 */
@Composable
fun KitWebView(
    url: String,
    modifier: Modifier = Modifier,
    javaScriptEnabled: Boolean = false,
) {
    val savedUrl = remember(url) { url }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = javaScriptEnabled
                settings.domStorageEnabled = javaScriptEnabled
                loadUrl(savedUrl)
            }
        },
        update = { view ->
            if (view.url != savedUrl) view.loadUrl(savedUrl)
        },
    )
}
