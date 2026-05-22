package dev.shipkaro.kit.core.config

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Supabase-backed [RemoteAppConfig].
 *
 * Reads a single row from a public `app_config` Postgres table on Supabase. The
 * table should expose one row per app environment with kit-aligned columns:
 *  - `min_supported_version` (int4)
 *  - `latest_version` (int4)
 *  - `latest_version_name` (text)
 *  - `maintenance_mode` (bool, default `false`)
 *  - `maintenance_message` (text)
 *  - `app_changelog` (jsonb)
 *  - `updated_at` (timestamptz, default `now()`)
 *
 * The row with the most recent `updated_at` wins. Make the table publicly
 * readable with an RLS select policy `using (true)` — the kit reads it without
 * authenticating.
 *
 * Reactive like [FirebaseRemoteAppConfig]: bumps a generation counter after each
 * refresh so existing flow collectors re-emit fresh values.
 */
class SupabaseRemoteAppConfig(private val client: SupabaseClient) : RemoteAppConfig {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val generation = MutableStateFlow(0)

    @Volatile
    private var snapshot: Map<String, String> = emptyMap()

    init { refresh() }

    /** Re-pull the latest row. Safe to call from anywhere — runs on IO. */
    fun refresh() {
        scope.launch {
            runCatching {
                val row = client.from("app_config")
                    .select {
                        order("updated_at", order = Order.DESCENDING)
                        limit(1)
                    }
                    .decodeSingle<AppConfigRow>()
                snapshot = row.toMap()
                generation.value++
            }
        }
    }

    override fun isEnabled(key: String, default: Boolean): Flow<Boolean> =
        generation.map {
            snapshot[key]?.equals("true", ignoreCase = true) ?: default
        }

    override fun stringValue(key: String, default: String): Flow<String> =
        generation.map {
            snapshot[key]?.takeIf { it.isNotEmpty() } ?: default
        }

    @Serializable
    private data class AppConfigRow(
        @SerialName("min_supported_version") val minSupportedVersion: Int? = null,
        @SerialName("latest_version") val latestVersion: Int? = null,
        @SerialName("latest_version_name") val latestVersionName: String? = null,
        @SerialName("maintenance_mode") val maintenanceMode: Boolean = false,
        @SerialName("maintenance_message") val maintenanceMessage: String? = null,
        @SerialName("app_changelog") val appChangelog: JsonElement? = null,
    ) {
        fun toMap(): Map<String, String> = buildMap {
            minSupportedVersion?.let { put("min_supported_version", it.toString()) }
            latestVersion?.let { put("latest_version", it.toString()) }
            latestVersionName?.let { put("latest_version_name", it) }
            put("maintenance_mode", maintenanceMode.toString())
            maintenanceMessage?.let { put("maintenance_message", it) }
            appChangelog?.let { put("app_changelog", it.toString()) }
        }
    }
}
