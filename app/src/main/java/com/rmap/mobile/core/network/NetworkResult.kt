package com.rmap.mobile.core.network

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
        val code: Int
    ) : NetworkResult<T>

    data class Error(
        val message: String,
        val code: Int? = null,
        val type: NetworkErrorType,
        val apiCode: Int? = null,
        val cause: Throwable? = null
    ) : NetworkResult<Nothing>
}

class AppException(
    override val message: String,
    val code: Int? = null,
    val type: NetworkErrorType,
    val apiCode: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)

inline fun <T, R> NetworkResult<T>.toDomainResult(
    mapper: (T) -> R
): Result<R> {
    return when (this) {
        is NetworkResult.Success -> runCatching { mapper(data) }
        is NetworkResult.Error -> Result.failure(toAppException())
    }
}

fun NetworkResult.Error.toAppException(): AppException = AppException(
    message = message,
    code = code,
    type = type,
    apiCode = apiCode,
    cause = cause
)

enum class NetworkErrorType {
    Unauthorized,
    Forbidden,
    NotFound,
    Server,
    EmptyBody,
    NoInternet,
    Timeout,
    Serialization,
    Unknown
}
