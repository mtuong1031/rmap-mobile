package com.rmap.mobile.core.network

import com.rmap.mobile.BuildConfig
import okhttp3.CookieJar
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(
    baseUrl: String,
    cookieJar: CookieJar,
    enableHttpLogging: Boolean = BuildConfig.DEBUG
) {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .apply {
            if (enableHttpLogging) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
            }
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(normalizeBaseUrl(baseUrl))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> createService(): T = createService(T::class.java)

    companion object {
        fun fromBuildConfig(cookieJar: CookieJar): ApiClient {
            return ApiClient(
                baseUrl = BuildConfig.BASE_URL,
                cookieJar = cookieJar,
                enableHttpLogging = BuildConfig.DEBUG
            )
        }

        private fun normalizeBaseUrl(baseUrl: String): String {
            require(baseUrl.isNotBlank()) { "BASE_URL must not be blank" }

            val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val httpUrl = normalizedBaseUrl.toHttpUrlOrNull()
                ?: error("BASE_URL must be a valid HTTP or HTTPS URL")

            require(httpUrl.scheme == "http" || httpUrl.scheme == "https") {
                "BASE_URL must use HTTP or HTTPS"
            }
            require(httpUrl.encodedPath.endsWith("/")) {
                "BASE_URL must end with a slash"
            }

            return normalizedBaseUrl
        }

        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 180L
        private const val WRITE_TIMEOUT_SECONDS = 60L
        private const val CALL_TIMEOUT_SECONDS = 180L
    }
}
