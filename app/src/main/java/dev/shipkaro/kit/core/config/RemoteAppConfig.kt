package dev.shipkaro.kit.core.config

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * RUNTIME APP-CONFIG (separate from [KitConfig]).
 *
 * This is the per-app, per-release flag system the END developer wires to
 * Supabase Remote Config / Firebase Remote Config for the app they build with
 * this kit. It lets THEM toggle behaviour or run a kill switch without shipping
 * a new APK.
 *
 * The kit only provides the contract + a local no-op default so the template
 * compiles and runs offline. Phase 4 swaps in the Supabase/Firebase impl.
 */
interface RemoteAppConfig {
    fun isEnabled(key: String, default: Boolean = false): Flow<Boolean>
    fun stringValue(key: String, default: String = ""): Flow<String>
}

/** Default impl: returns provided defaults. Replaced in Phase 4 (Ops). */
class LocalRemoteAppConfig : RemoteAppConfig {
    override fun isEnabled(key: String, default: Boolean): Flow<Boolean> = flowOf(default)
    override fun stringValue(key: String, default: String): Flow<String> = flowOf(default)
}
