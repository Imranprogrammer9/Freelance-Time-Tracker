package com.freelance.timetracker.core.push

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.freelance.timetracker.MainActivity
import com.freelance.timetracker.R

/**
 * FCM entry point. Inert until Firebase is set up (`google-services.json` + plugin) —
 * without it the device never registers for FCM so this service is simply never called.
 *
 * Displays `notification`-payload messages as a system notification; tapping opens
 * [MainActivity] with any `deeplink` data key forwarded as an intent extra (route it
 * from MainActivity).
 */
class KitMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // TODO: forward [token] to your backend so it can target this device.
        // Supabase: upsert into a device_tokens table. Firebase: store under the user.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        showNotification(
            title = notification.title.orEmpty(),
            body = notification.body.orEmpty(),
            deeplink = message.data[DEEPLINK_KEY],
        )
    }

    private fun showNotification(title: String, body: String, deeplink: String?) {
        // POST_NOTIFICATIONS is a runtime permission on Android 13+; bail if not granted.
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return

        KitNotifications.ensureDefaultChannel(this)

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            deeplink?.let { putExtra(DEEPLINK_KEY, it) }
        }
        val pending = PendingIntent.getActivity(
            this,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(this, KitNotifications.DEFAULT_CHANNEL_ID)
            // Replace with a dedicated monochrome status-bar icon for production.
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        /** Data-payload key carrying an in-app route for notification taps. */
        const val DEEPLINK_KEY = "deeplink"
    }
}
