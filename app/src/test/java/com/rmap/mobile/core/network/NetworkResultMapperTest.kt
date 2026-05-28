package com.rmap.mobile.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkResultMapperTest {
    @Test
    fun `toDomainResult maps success data`() {
        val result = NetworkResult.Success(
            data = TestDto(value = "roadmap"),
            code = 200
        ).toDomainResult { dto ->
            TestDomain(value = dto.value.uppercase())
        }

        assertTrue(result.isSuccess)
        assertEquals(TestDomain("ROADMAP"), result.getOrNull())
    }

    @Test
    fun `toDomainResult maps error to app exception`() {
        val result = NetworkResult.Error(
            message = "Không tìm thấy dữ liệu.",
            code = 404,
            type = NetworkErrorType.NotFound,
            apiCode = 40400
        ).toDomainResult { value: TestDto ->
            TestDomain(value.value)
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is AppException)
        val appException = exception as AppException
        assertEquals("Không tìm thấy dữ liệu.", appException.message)
        assertEquals(404, appException.code)
        assertEquals(40400, appException.apiCode)
        assertEquals(NetworkErrorType.NotFound, appException.type)
    }

    private data class TestDto(
        val value: String
    )

    private data class TestDomain(
        val value: String
    )
}
