package dev.shipkaro.kit.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Per-app language switching. Backed by AndroidX AppCompat, which persists the
 * choice and applies it on Android 13+ via the system per-app language API, and
 * emulates it on older versions.
 *
 * To add a language: create res/values-<tag>/strings.xml, add the tag to
 * resourceConfigurations in app/build.gradle.kts and to res/xml/locales_config.xml.
 * Everything below keeps working with zero code changes.
 */
object LocaleManager {

    /** Languages the kit ships with. Keep in sync with locales_config.xml. */
    val supported: List<Language> = listOf(
        Language(tag = "en", displayName = "English"),
        Language(tag = "ur", displayName = "اردو"),
    )

    data class Language(val tag: String, val displayName: String)

    /** Apply a language by BCP-47 tag, or empty string to follow system. */
    fun setLanguage(tag: String) {
        val locales =
            if (tag.isBlank()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun currentTag(): String =
        AppCompatDelegate.getApplicationLocales().toLanguageTags()
}
