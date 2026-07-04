package com.freelance.timetracker.core.ops

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One published release in the app changelog.
 *
 * Authored on the backend (Firebase Remote Config / Supabase) as a JSON array under the
 * `app_changelog` key and parsed by [ChangelogManager]. Publishing the changelog remotely
 * means you can show release notes for an update the user has not installed yet — the
 * update sheet renders the notes for versions newer than the running build.
 *
 * Example backend value:
 * ```json
 * [
 *   { "version_code": 4, "version_name": "1.3.0", "date": "2026-06-01",
 *     "notes": ["Dark mode polish", "Faster sync"] }
 * ]
 * ```
 */
@Serializable
data class ChangelogRelease(
    @SerialName("version_code") val versionCode: Int = 0,
    @SerialName("version_name") val versionName: String = "",
    val date: String = "",
    val notes: List<String> = emptyList(),
)
