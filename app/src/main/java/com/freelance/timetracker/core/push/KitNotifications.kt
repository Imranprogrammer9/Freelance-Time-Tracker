package com.freelance.timetracker.core.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/** Notification channel setup. minSdk 26 means channels are always available. */
object KitNotifications {

    /** Default channel for FCM messages. Apps add more channels as needed. */
    const val DEFAULT_CHANNEL_ID = "default"

    /** Idempotent — safe to call on every notification. */
    fun ensureDefaultChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(DEFAULT_CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                DEFAULT_CHANNEL_ID,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
    }
}
