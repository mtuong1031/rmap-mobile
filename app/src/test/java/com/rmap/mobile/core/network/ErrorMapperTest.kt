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
        assertEquals("Máy chủ đang gặp sự cố. Vui lòng thử lại sau.", error.message)
    }

    @Test
    fun `fromHttp maps unauthorized`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 401,
            errorBody = """{"code":40100,"message":"Authentication required."}"""
        )

        assertEquals(NetworkErrorType.Unauthorized, error.type)
        assertEquals(401, error.code)
        assertEquals("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", error.message)
    }

    @Test
    fun `fromHttp maps forbidden`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 403,
            errorBody = """{"code":40300,"message":"Forbidden"}"""
        )

        assertEquals(NetworkErrorType.Forbidden, error.type)
        assertEquals("Bạn không có quyền truy cập.", error.message)
    }

    @Test
    fun `fromHttp maps not found`() {
        val error = ErrorMapper.fromHttp(
            statusCode = 404,
            errorBody = """{"code":40400,"message":"Missing"}"""
        )

        assertEquals(NetworkErrorType.NotFound, error.type)
        assertEquals("Không tìm thấy dữ liệu.", error.message)
    }

    @Test
    fun `fromException maps timeout`() {
        val error = ErrorMapper.fromException(SocketTimeoutException("timeout"))

        assertEquals(NetworkErrorType.Timeout, error.type)
        assertEquals("Máy chủ phản hồi quá lâu. Vui lòng thử lại.", error.message)
    }

    @Test
    fun `fromException maps no internet exception`() {
        val error = ErrorMapper.fromException(UnknownHostException("offline"))

        assertEquals(NetworkErrorType.NoInternet, error.type)
        assertEquals("Không có kết nối mạng. Vui lòng kiểm tra Internet rồi thử lại.", error.message)
    }

    @Test
    fun `fromException maps json syntax exception`() {
        val error = ErrorMapper.fromException(JsonSyntaxException("bad json"))

        assertEquals(NetworkErrorType.Serialization, error.type)
        assertEquals("Dữ liệu phản hồi không hợp lệ. Vui lòng thử lại sau.", error.message)
    }
}
