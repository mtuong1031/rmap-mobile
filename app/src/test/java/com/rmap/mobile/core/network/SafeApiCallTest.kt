package com.rmap.mobile.core.network

import com.google.gson.JsonSyntaxException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SafeApiCallTest {
    @Test
    fun `execute returns success for successful response body`() = runTest {
        val result = SafeApiCall.execute {
            Response.success("ok")
        }

        assertTrue(result is NetworkResult.Success)
        val success = result as NetworkResult.Success
        assertEquals("ok", success.data)
        assertEquals(200, success.code)
    }

    @Test
    fun `execute returns empty body error for successful response without body`() = runTest {
        val result = SafeApiCall.execute<String> {
            Response.success(null)
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.EmptyBody, error.type)
        assertEquals(200, error.code)
    }

    @Test
    fun `execute maps unauthorized response and calls unauthorized callback`() = runTest {
        var unauthorizedCallCount = 0

        val result = SafeApiCall.execute<String> {
            Response.error(
                401,
                """{"code":40101,"message":"Invalid email or password"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }

        val resultWithCallback = SafeApiCall.execute(
            onUnauthorized = { unauthorizedCallCount++ }
        ) {
            Response.error(
                401,
                """{"code":40101,"message":"Invalid email or password"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.Unauthorized, error.type)
        assertEquals(401, error.code)
        assertEquals(40101, error.apiCode)
        assertEquals("Please sign in to continue.", error.message)
        assertTrue(resultWithCallback is NetworkResult.Error)
        assertEquals(1, unauthorizedCallCount)
    }

    @Test
    fun `execute maps forbidden response`() = runTest {
        val result = SafeApiCall.execute<String> {
            Response.error(
                403,
                """{"code":40300,"message":"Forbidden"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.Forbidden, error.type)
        assertEquals("You do not have permission to access this.", error.message)
    }

    @Test
    fun `execute maps not found response`() = runTest {
        val result = SafeApiCall.execute<String> {
            Response.error(
                404,
                """{"code":40400,"message":"Missing"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.NotFound, error.type)
        assertEquals("No data was found.", error.message)
    }

    @Test
    fun `execute maps server error response`() = runTest {
        val result = SafeApiCall.execute<String> {
            Response.error(
                500,
                """{"code":50000,"message":"Internal stack details"}"""
                    .toResponseBody("application/json".toMediaType())
            )
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.Server, error.type)
        assertEquals("The server is having trouble. Please try again later.", error.message)
    }

    @Test
    fun `execute maps timeout exception`() = runTest {
        val result = SafeApiCall.execute<String> {
            throw SocketTimeoutException("timeout")
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.Timeout, error.type)
    }

    @Test
    fun `execute maps no internet exception`() = runTest {
        val result = SafeApiCall.execute<String> {
            throw UnknownHostException("offline")
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.NoInternet, error.type)
    }

    @Test
    fun `execute maps malformed json exception`() = runTest {
        val result = SafeApiCall.execute<String> {
            throw JsonSyntaxException("bad json")
        }

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(NetworkErrorType.Serialization, error.type)
    }
}
