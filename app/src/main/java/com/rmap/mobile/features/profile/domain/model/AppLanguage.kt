package com.rmap.mobile.features.profile.domain.model

/**
 * Supported application languages for per-app locale switching.
 *
 * @property tag BCP 47 language tag used by [androidx.appcompat.app.AppCompatDelegate.setApplicationLocales].
 * @property displayName Native name shown to the user in the language picker.
 * @property flag Emoji flag for visual identification.
 */
enum class AppLanguage(val tag: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "🇬🇧"),
    VIETNAMESE("vi", "Tiếng Việt", "🇻🇳");

    companion object {
        /**
         * Resolves the current [AppLanguage] from the active application locales.
         * Falls back to [ENGLISH] when no matching locale is found.
         */
        fun fromTag(tag: String?): AppLanguage {
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: ENGLISH
        }
    }
}
