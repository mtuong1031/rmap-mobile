package com.rmap.mobile.core.network

import com.rmap.mobile.core.storage.SessionCookieStorage
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class SessionCookieJar(
    private val storage: SessionCookieStorage
) : CookieJar {
    private val lock = Any()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(lock) {
            val existingCookies = loadStoredCookies()
            val replacementKeys = cookies.map { it.identityKey() }.toSet()
            val mergedCookies = existingCookies
                .filterNot { it.cookie.identityKey() in replacementKeys }
                .plus(cookies.map { StoredCookie(url = url, cookie = it) })
                .filterNot { it.cookie.hasExpired() }

            storage.save(mergedCookies.map { it.encode() }.toSet())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return synchronized(lock) {
            val storedCookies = loadStoredCookies()
            val validCookies = storedCookies.filterNot { it.cookie.hasExpired() }
            if (validCookies.size != storedCookies.size) {
                storage.save(validCookies.map { it.encode() }.toSet())
            }
            validCookies
                .map { it.cookie }
                .filter { it.matches(url) }
        }
    }

    fun clear() {
        synchronized(lock) {
            storage.clear()
        }
    }

    fun hasStoredCookies(): Boolean {
        return synchronized(lock) {
            loadStoredCookies().isNotEmpty()
        }
    }

    private fun loadStoredCookies(): List<StoredCookie> {
        return storage.load().mapNotNull(StoredCookie::decode)
    }

    private data class StoredCookie(
        val url: HttpUrl,
        val cookie: Cookie
    ) {
        fun encode(): String = "${url}\t${cookie}"

        companion object {
            fun decode(value: String): StoredCookie? {
                val parts = value.split("\t", limit = 2)
                if (parts.size != 2) return null

                val url = parts[0].toHttpUrlOrNull() ?: return null
                val cookie = Cookie.parse(url, parts[1]) ?: return null
                return StoredCookie(url = url, cookie = cookie)
            }
        }
    }
}

private fun Cookie.identityKey(): String = "$name|$domain|$path"

private fun Cookie.hasExpired(): Boolean = expiresAt < System.currentTimeMillis()
