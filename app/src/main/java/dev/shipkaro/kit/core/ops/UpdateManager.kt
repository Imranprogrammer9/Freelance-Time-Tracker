package dev.shipkaro.kit.core.ops

import dev.shipkaro.kit.BuildConfig
import dev.shipkaro.kit.core.config.RemoteAppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * App-update gate ("kill switch"). Compares this build's [BuildConfig.VERSION_CODE]
 * against version floors published in [RemoteAppConfig].
 *
 * Remote keys (set them in Firebase Remote Config etc.):
 *  - `min_supported_version` : version codes below this are forced to update ([UpdateState.Required])
 *  - `latest_version`        : below this (but >= min) prompts an optional update ([UpdateState.Optional])
 *
 * Both default to 0 so an unconfigured backend never blocks the app.
 */
class UpdateManager(remoteConfig: RemoteAppConfig) {

    enum class UpdateState { NONE, OPTIONAL, REQUIRED }

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

    private companion object {
        const val KEY_MIN_VERSION = "min_supported_version"
        const val KEY_LATEST_VERSION = "latest_version"
    }
}
