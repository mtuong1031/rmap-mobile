package com.rmap.mobile.core.network

import com.google.gson.JsonSyntaxException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMapperTest {
    @Test
    fun `fromHttp parses api error body`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 409,
            errorBody = """{"code":40901,"message":"Email already registered"}"""
        )

        assertEquals(NetworkErrorType.Unknown, error.type)
        assertEquals(409, error.code)
        assertEquals(40901, error.apiCode)
        assertEquals("Email already registered", error.message)
    }

    @Test
    fun `fromHttp falls back to status message when error body is malformed`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 500,
            errorBody = "not json"
        )

        assertEquals(NetworkErrorType.Server, error.type)
        assertEquals(500, error.code)
        assertEquals("The server is having trouble. Please try again later.", error.message)
    }

    @Test
    fun `fromHttp maps unauthorized`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 401,
            errorBody = """{"code":40100,"message":"Authentication required."}"""
        )

        assertEquals(NetworkErrorType.Unauthorized, error.type)
        assertEquals(401, error.code)
        assertEquals("Please sign in to continue.", error.message)
    }

    @Test
    fun `fromHttp maps forbidden`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 403,
            errorBody = """{"code":40300,"message":"Forbidden"}"""
        )

        assertEquals(NetworkErrorType.Forbidden, error.type)
        assertEquals("You do not have permission to access this.", error.message)
    }

    @Test
    fun `fromHttp maps not found`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 404,
            errorBody = """{"code":40400,"message":"Missing"}"""
        )

        assertEquals(NetworkErrorType.NotFound, error.type)
        assertEquals("No data was found.", error.message)
    }

    @Test
    fun `fromException maps timeout`() {
        val error = ErrorMapper.fromException(SocketTimeoutException("timeout"))

        assertEquals(NetworkErrorType.Timeout, error.type)
        assertEquals("The server took too long to respond. Please try again.", error.message)
    }

    @Test
    fun `fromException maps no internet exception`() {
        val error = ErrorMapper.fromException(UnknownHostException("offline"))

        assertEquals(NetworkErrorType.NoInternet, error.type)
        assertEquals("No internet connection. Please check your connection and try again.", error.message)
    }

    @Test
    fun `fromException maps json syntax exception`() {
        val error = ErrorMapper.fromException(JsonSyntaxException("bad json"))

        assertEquals(NetworkErrorType.Serialization, error.type)
        assertEquals("The response data was invalid. Please try again later.", error.message)
    }
}
