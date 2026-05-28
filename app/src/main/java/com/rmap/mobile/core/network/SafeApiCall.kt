package com.rmap.mobile.core.network

import retrofit2.Response

object SafeApiCall {
    suspend fun <T : Any> execute(
        onUnauthorized: suspend () -> Unit = {},
        call: suspend () -> Response<T>
    ): NetworkResult<T> {
        return runCatching {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(
                        data = body,
                        code = response.code()
                    )
                } else {
                    ErrorMapper.emptyBody(response.code())
                }
            } else {
                val error = ErrorMapper.fromHttp(
                    statusCode = response.code(),
                    errorBody = response.errorBody()?.string()
                )
                if (error.type == NetworkErrorType.Unauthorized) {
                    onUnauthorized()
                }
                error
            }
        }.getOrElse { throwable ->
            ErrorMapper.fromException(throwable)
        }
    }

    suspend fun executeUnit(
        onUnauthorized: suspend () -> Unit = {},
        call: suspend () -> Response<Unit>
    ): NetworkResult<Unit> {
        return runCatching {
            val response = call()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    data = Unit,
                    code = response.code()
                )
            } else {
                val error = ErrorMapper.fromHttp(
                    statusCode = response.code(),
                    errorBody = response.errorBody()?.string()
                )
                if (error.type == NetworkErrorType.Unauthorized) {
                    onUnauthorized()
                }
                error
            }
        }.getOrElse { throwable ->
            ErrorMapper.fromException(throwable)
        }
    }
}
