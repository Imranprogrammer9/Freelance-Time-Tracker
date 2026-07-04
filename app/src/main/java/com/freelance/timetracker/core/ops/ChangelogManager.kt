package com.freelance.timetracker.core.ops

import com.freelance.timetracker.core.config.RemoteAppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Reads the app changelog published on the backend via [RemoteAppConfig].
 *
 * Remote key: `app_changelog` — a JSON array of [ChangelogRelease]. Editing it on the
 * backend updates the in-app "What's New" screen and the update sheet without a new APK.
 *
 * Until a backend value is set (LOCAL provider, or Firebase before `google-services.json`),
 * [SAMPLE_CHANGELOG] is returned so the changelog screen is demonstrable offline.
 */
class ChangelogManager(remoteConfig: RemoteAppConfig) {

    private val json = Json { ignoreUnknownKeys = true }

    /** All releases, newest first. Empty if the backend value is missing or malformed. */
    val releases: Flow<List<ChangelogRelease>> =
        remoteConfig.stringValue(KEY_CHANGELOG, SAMPLE_CHANGELOG).map { raw ->
            runCatching { json.decodeFromString<List<ChangelogRelease>>(raw) }
                .getOrDefault(emptyList())
                .sortedByDescending { it.versionCode }
        }

    companion object {
        const val KEY_CHANGELOG = "app_changelog"

        /** Seed shown when no backend changelog is configured — replace with your own. */
        val SAMPLE_CHANGELOG = """
            [
              { "version_code": 3, "version_name": "1.2.0", "date": "2026-05-20",
                "notes": ["New paywall design", "Faster app startup", "Bug fixes"] },
              { "version_code": 2, "version_name": "1.1.0", "date": "2026-05-10",
                "notes": ["Added Google sign-in", "Dark mode improvements"] },
              { "version_code": 1, "version_name": "1.0.0", "date": "2026-05-01",
                "notes": ["First release"] }
            ]
        """.trimIndent()
    }
}
