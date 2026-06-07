package com.rmap.mobile.core.network

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {
    fun fromHttp(statusCode: Int, errorBody: String?): NetworkResult.Error {
        val apiError = errorBody?.let(::parseApiError)
        return NetworkResult.Error(
            type = statusCode.toErrorType(),
            message = statusCode.toUserMessage(apiError?.message),
            code = statusCode,
            apiCode = apiError?.code
        )
    }

    fun emptyBody(statusCode: Int): NetworkResult.Error {
        return NetworkResult.Error(
            type = NetworkErrorType.EmptyBody,
            message = "The server returned no data. Please try again later.",
            code = statusCode
        )
    }

    fun fromException(throwable: Throwable): NetworkResult.Error {
        return when (throwable) {
            is SocketTimeoutException -> NetworkResult.Error(
                type = NetworkErrorType.Timeout,
                message = "The server took too long to respond. Please try again.",
                cause = throwable
            )

            is JsonSyntaxException,
            is MalformedJsonException -> NetworkResult.Error(
                type = NetworkErrorType.Serialization,
                message = "The response data was invalid. Please try again later.",
                cause = throwable
            )

            is UnknownHostException,
            is IOException -> NetworkResult.Error(
                type = NetworkErrorType.NoInternet,
                message = "No internet connection. Please check your connection and try again.",
                cause = throwable
            )

            else -> NetworkResult.Error(
                type = NetworkErrorType.Unknown,
                message = "Something went wrong. Please try again.",
                cause = throwable
            )
        }
    }

    private fun Int.toErrorType(): NetworkErrorType {
        return when (this) {
            401 -> NetworkErrorType.Unauthorized
            403 -> NetworkErrorType.Forbidden
            404 -> NetworkErrorType.NotFound
            in 500..599 -> NetworkErrorType.Server
            else -> NetworkErrorType.Unknown
        }
    }

    private fun Int.toUserMessage(apiMessage: String?): String {
        return when (this) {
            401 -> "Your session has expired. Please sign in again."
            403 -> "You do not have permission to access this."
            404 -> "No data was found."
            in 500..599 -> "The server is having trouble. Please try again later."
            else -> apiMessage ?: "Something went wrong. Please try again."
        }
    }

    private fun parseApiError(errorBody: String): ApiError? {
        return runCatching {
            val jsonObject = JsonParser.parseString(errorBody).asJsonObject
            ApiError(
                code = jsonObject.get("code")?.takeIf { !it.isJsonNull }?.asInt,
                message = jsonObject.get("message")?.takeIf { !it.isJsonNull }?.asString
            )
        }.getOrNull()
    }

    private data class ApiError(
        val code: Int?,
        val message: String?
    )
}
