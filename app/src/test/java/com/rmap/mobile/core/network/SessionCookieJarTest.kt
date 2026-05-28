package com.rmap.mobile.core.network

import com.rmap.mobile.core.storage.SessionCookieStorage
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionCookieJarTest {
    @Test
    fun `saveFromResponse stores cookies and loadForRequest returns matching cookies`() {
        val storage = InMemorySessionCookieStorage()
        val cookieJar = SessionCookieJar(storage)
        val responseUrl = "http://10.0.2.2:3001/api/v1/auth/login".toHttpUrl()
        val requestUrl = "http://10.0.2.2:3001/api/v1/users/me".toHttpUrl()
        val cookie = Cookie.Builder()
            .name("access_token")
            .value("abc")
            .domain("10.0.2.2")
            .path("/")
            .httpOnly()
            .expiresAt(System.currentTimeMillis() + ONE_HOUR_MS)
            .build()

        cookieJar.saveFromResponse(responseUrl, listOf(cookie))

        val loadedCookies = cookieJar.loadForRequest(requestUrl)
        assertEquals(1, loadedCookies.size)
        assertEquals("access_token", loadedCookies.first().name)
        assertEquals("abc", loadedCookies.first().value)
    }

    @Test
    fun `saveFromResponse replaces cookie with same identity`() {
        val storage = InMemorySessionCookieStorage()
        val cookieJar = SessionCookieJar(storage)
        val responseUrl = "http://10.0.2.2:3001/api/v1/auth/login".toHttpUrl()
        val requestUrl = "http://10.0.2.2:3001/api/v1/users/me".toHttpUrl()

        cookieJar.saveFromResponse(
            responseUrl,
            listOf(sessionCookie("access_token", "old"))
        )
        cookieJar.saveFromResponse(
            responseUrl,
            listOf(sessionCookie("access_token", "new"))
        )

        val loadedCookies = cookieJar.loadForRequest(requestUrl)
        assertEquals(1, loadedCookies.size)
        assertEquals("new", loadedCookies.first().value)
    }

    @Test
    fun `clear removes stored cookies`() {
        val storage = InMemorySessionCookieStorage()
        val cookieJar = SessionCookieJar(storage)
        val responseUrl = "http://10.0.2.2:3001/api/v1/auth/login".toHttpUrl()
        val requestUrl = "http://10.0.2.2:3001/api/v1/users/me".toHttpUrl()

        cookieJar.saveFromResponse(
            responseUrl,
            listOf(sessionCookie("access_token", "abc"))
        )
        cookieJar.clear()

        assertTrue(cookieJar.loadForRequest(requestUrl).isEmpty())
        assertTrue(storage.load().isEmpty())
    }

    private fun sessionCookie(name: String, value: String): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .domain("10.0.2.2")
            .path("/")
            .httpOnly()
            .expiresAt(System.currentTimeMillis() + ONE_HOUR_MS)
            .build()
    }

    private class InMemorySessionCookieStorage : SessionCookieStorage {
        private var cookies: Set<String> = emptySet()

        override fun load(): Set<String> = cookies

        override fun save(cookies: Set<String>) {
            this.cookies = cookies
        }

        override fun clear() {
            cookies = emptySet()
        }
    }

    private companion object {
        const val ONE_HOUR_MS = 60L * 60L * 1000L
    }
}
