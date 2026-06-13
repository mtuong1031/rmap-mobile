package com.rmap.mobile.core.network

import android.util.Log
import com.rmap.mobile.core.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Authenticator
import okhttp3.CookieJar
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val baseUrl: String,
    private val cookieJar: CookieJar,
    private val sessionManager: SessionManager
) : Authenticator {

    private val refreshClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Log.d("TokenAuthenticator", "Refresh token retry limit reached")
            return null
        }

        val path = response.request.url.encodedPath
        if (path.endsWith("auth/refresh") || path.endsWith("auth/logout")) {
            Log.d("TokenAuthenticator", "Token refresh skipped for path: $path")
            return null
        }

        val hasSession = when (cookieJar) {
            is SessionCookieJar -> cookieJar.hasStoredCookies()
            else -> true
        }

        if (!hasSession) {
            Log.d("TokenAuthenticator", "No active session cookies, skipping refresh")
            return null
        }

        synchronized(this) {
            Log.d("TokenAuthenticator", "Attempting to refresh token")
            val refreshUrl = baseUrl.toHttpUrlOrNull()?.newBuilder()
                ?.addPathSegments("auth/refresh")
                ?.build() ?: return null

            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post(ByteArray(0).toRequestBody(null, 0, 0))
                .build()

            try {
                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    Log.d("TokenAuthenticator", "Refresh token successful")
                    refreshResponse.close()
                    // Rebuilding the request will allow BridgeInterceptor to attach the new cookies
                    return response.request.newBuilder().build()
                } else {
                    Log.d("TokenAuthenticator", "Refresh token failed with code: ${refreshResponse.code}")
                    refreshResponse.close()
                    handleSessionExpired()
                    return null
                }
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Refresh token request failed with exception", e)
                handleSessionExpired()
                return null
            }
        }
    }

    private fun handleSessionExpired() {
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.handleUnauthorized()
        }
    }

    private fun responseCount(response: Response?): Int {
        var result = 1
        var current = response
        while (current?.priorResponse != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
}
