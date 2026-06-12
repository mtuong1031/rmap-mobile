package com.rmap.mobile.features.profile.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun `fromTag resolves supported languages case insensitively`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag("EN"))
        assertEquals(AppLanguage.VIETNAMESE, AppLanguage.fromTag("vi"))
    }

    @Test
    fun `fromTag falls back to English for missing or unsupported tags`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag(null))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag("fr"))
    }
}
