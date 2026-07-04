package com.freelance.timetracker.core.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Firebase Remote Config-backed [RemoteAppConfig].
 *
 * Inert until Firebase is set up (`google-services.json` + the google-services plugin):
 * [FirebaseRemoteConfig.getInstance] throws without it, so the config falls back to the
 * caller-supplied defaults.
 *
 * Remote Config is pull-based, not reactive. After each `fetchAndActivate`, [generation]
 * is bumped so existing [isEnabled] / [stringValue] collectors re-read the fresh values.
 */
class FirebaseRemoteAppConfig : RemoteAppConfig {

    private val remoteConfig: FirebaseRemoteConfig? =
        runCatching { FirebaseRemoteConfig.getInstance() }.getOrNull()

    // Bumped after every successful fetch so downstream flows re-emit.
    private val generation = MutableStateFlow(0)

    init {
        remoteConfig?.fetchAndActivate()?.addOnCompleteListener { generation.value++ }
    }

    /** Re-pull values from the Remote Config backend. */
    fun refresh() {
        remoteConfig?.fetchAndActivate()?.addOnCompleteListener { generation.value++ }
    }

    override fun isEnabled(key: String, default: Boolean): Flow<Boolean> =
        generation.map { remoteConfig?.getBoolean(key) ?: default }

    override fun stringValue(key: String, default: String): Flow<String> =
        generation.map {
            remoteConfig?.getString(key)?.takeIf { it.isNotEmpty() } ?: default
        }
}
