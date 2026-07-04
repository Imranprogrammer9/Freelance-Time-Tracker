package com.freelance.timetracker.core.permission

import android.Manifest

/**
 * Runtime permissions the kit's permission helper supports out of the box.
 *
 * NOTIFICATIONS works immediately — the kit already declares `POST_NOTIFICATIONS` in the
 * manifest (for FCM). To use CAMERA or LOCATION, add the matching `<uses-permission>` to
 * `AndroidManifest.xml` first, otherwise the request resolves straight to denied.
 *
 * Add your own entries here (and a manifest declaration) for any other permission.
 */
enum class KitPermission(val manifestPermission: String) {
    NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),
    CAMERA(Manifest.permission.CAMERA),
    MICROPHONE(Manifest.permission.RECORD_AUDIO),

    /**
     * Image-gallery access. Manifest string resolves at runtime per Android version inside
     * [KitPermissionState]: `READ_MEDIA_IMAGES` on Android 13+ (API 33), legacy
     * `READ_EXTERNAL_STORAGE` on API 26..32. The constant here is the modern one; the state
     * helper substitutes the legacy permission when the device is on an older SDK.
     */
    PHOTO(Manifest.permission.READ_MEDIA_IMAGES),
    CONTACTS(Manifest.permission.READ_CONTACTS),
    CALENDAR(Manifest.permission.READ_CALENDAR),
    LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),

    /**
     * Step-counter / workout detection. Required from Android 10 (API 29); granted implicitly
     * on older devices (the state helper short-circuits to GRANTED in that case).
     */
    MOTION(Manifest.permission.ACTIVITY_RECOGNITION),
}
