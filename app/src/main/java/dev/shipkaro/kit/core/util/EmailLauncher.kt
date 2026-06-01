package dev.shipkaro.kit.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

/**
 * Opens the user's default email composer prefilled with [to] / [subject] /
 * [body]. Uses the `mailto:` URI scheme so any registered mail app (Gmail,
 * Outlook, FairEmail, etc.) handles it.
 *
 * Falls back to a chooser when multiple mail apps are installed. No-op when no
 * mail app is installed (logged via Timber, not thrown — common on emulators).
 */
object EmailLauncher {

    fun send(
        context: Context,
        to: String,
        subject: String? = null,
        body: String? = null,
        chooserTitle: String? = null,
    ) {
        val uri = buildString {
            append("mailto:")
            append(Uri.encode(to))
            val params = listOfNotNull(
                subject?.takeIf { it.isNotBlank() }?.let { "subject=${Uri.encode(it)}" },
                body?.takeIf { it.isNotBlank() }?.let { "body=${Uri.encode(it)}" },
            )
            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }.toUri()

        val intent = Intent(Intent.ACTION_SENDTO, uri)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val launchable = if (chooserTitle != null) {
            Intent.createChooser(intent, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } else {
            intent
        }

        runCatching { context.startActivity(launchable) }
            .onFailure { Timber.tag("Email").w(it, "No mail app handled mailto:%s", to) }
    }
}

private fun String.toUri(): Uri = Uri.parse(this)
