package com.rmap.mobile.core.storage

import android.content.Context

interface SessionCookieStorage {
    fun load(): Set<String>
    fun save(cookies: Set<String>)
    fun clear()
}

class SharedPreferencesSessionCookieStorage(
    context: Context
) : SessionCookieStorage {
    private val sharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun load(): Set<String> {
        return sharedPreferences.getStringSet(KEY_COOKIES, emptySet())?.toSet().orEmpty()
    }

    override fun save(cookies: Set<String>) {
        sharedPreferences.edit()
            .putStringSet(KEY_COOKIES, cookies)
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove(KEY_COOKIES)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "rmap_session_cookies"
        const val KEY_COOKIES = "cookies"
    }
}
