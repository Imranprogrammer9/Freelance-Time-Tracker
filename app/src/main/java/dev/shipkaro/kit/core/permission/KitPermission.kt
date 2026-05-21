package dev.shipkaro.kit.core.permission

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
    LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
}
