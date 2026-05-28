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
            message = "Máy chủ không trả về dữ liệu. Vui lòng thử lại sau.",
            code = statusCode
        )
    }

    fun fromException(throwable: Throwable): NetworkResult.Error {
        return when (throwable) {
            is SocketTimeoutException -> NetworkResult.Error(
                type = NetworkErrorType.Timeout,
                message = "Máy chủ phản hồi quá lâu. Vui lòng thử lại.",
                cause = throwable
            )

            is JsonSyntaxException,
            is MalformedJsonException -> NetworkResult.Error(
                type = NetworkErrorType.Serialization,
                message = "Dữ liệu phản hồi không hợp lệ. Vui lòng thử lại sau.",
                cause = throwable
            )

            is UnknownHostException,
            is IOException -> NetworkResult.Error(
                type = NetworkErrorType.NoInternet,
                message = "Không có kết nối mạng. Vui lòng kiểm tra Internet rồi thử lại.",
                cause = throwable
            )

            else -> NetworkResult.Error(
                type = NetworkErrorType.Unknown,
                message = "Đã xảy ra lỗi. Vui lòng thử lại.",
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
            401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
            403 -> "Bạn không có quyền truy cập."
            404 -> "Không tìm thấy dữ liệu."
            in 500..599 -> "Máy chủ đang gặp sự cố. Vui lòng thử lại sau."
            else -> apiMessage ?: "Đã xảy ra lỗi. Vui lòng thử lại."
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
