package com.freelance.timetracker.core.ops

import com.freelance.timetracker.BuildConfig
import com.freelance.timetracker.core.config.RemoteAppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * App-update + maintenance gate ("kill switch"). Compares this build's
 * [BuildConfig.VERSION_CODE] against version floors published in [RemoteAppConfig],
 * and surfaces a backend-triggered maintenance mode.
 *
 * Remote keys (set them in Firebase Remote Config / Supabase etc.):
 *  - `min_supported_version` : version codes below this are forced to update ([UpdateState.REQUIRED])
 *  - `latest_version`        : below this (but >= min) prompts an optional update ([UpdateState.OPTIONAL])
 *  - `latest_version_name`   : display name of the latest version (e.g. "1.3.0"), shown in the sheet
 *  - `maintenance_mode`      : when true, the whole app is blocked by a maintenance screen
 *  - `maintenance_message`   : optional custom message shown on the maintenance screen
 *
 * Versions default to 0 so an unconfigured backend never blocks the app; maintenance
 * defaults to false.
 */
class UpdateManager(remoteConfig: RemoteAppConfig) {

    enum class UpdateState { NONE, OPTIONAL, REQUIRED }

    /** Backend-triggered maintenance mode. [active] true blocks the app entirely. */
    data class Maintenance(val active: Boolean, val message: String)

    val updateState: Flow<UpdateState> = combine(
        remoteConfig.stringValue(KEY_MIN_VERSION, "0"),
        remoteConfig.stringValue(KEY_LATEST_VERSION, "0"),
    ) { minStr, latestStr ->
        val current = BuildConfig.VERSION_CODE
        val min = minStr.toIntOrNull() ?: 0
        val latest = latestStr.toIntOrNull() ?: 0
        when {
            current < min -> UpdateState.REQUIRED
            current < latest -> UpdateState.OPTIONAL
            else -> UpdateState.NONE
        }
    }

    /** Display name of the latest published version, or empty if the backend has none. */
    val latestVersionName: Flow<String> = remoteConfig.stringValue(KEY_LATEST_VERSION_NAME, "")

    val maintenance: Flow<Maintenance> = combine(
        remoteConfig.isEnabled(KEY_MAINTENANCE, false),
        remoteConfig.stringValue(KEY_MAINTENANCE_MESSAGE, ""),
    ) { active, message ->
        Maintenance(active, message)
    }

    private companion object {
        const val KEY_MIN_VERSION = "min_supported_version"
        const val KEY_LATEST_VERSION = "latest_version"
        const val KEY_LATEST_VERSION_NAME = "latest_version_name"
        const val KEY_MAINTENANCE = "maintenance_mode"
        const val KEY_MAINTENANCE_MESSAGE = "maintenance_message"
    }
}
