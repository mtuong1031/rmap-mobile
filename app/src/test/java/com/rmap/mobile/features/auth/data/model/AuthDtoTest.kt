package com.rmap.mobile.features.auth.data.model

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.rmap.mobile.features.auth.data.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthDtoTest {
    private val gson = Gson()

    @Test
    fun `register request serializes full name as camel case`() {
        val json = gson.toJson(
            RegisterRequestDto(
                email = "learner@example.com",
                password = "password123",
                fullName = "RMap Learner"
            )
        )

        val jsonObject = JsonParser.parseString(json).asJsonObject
        assertTrue(jsonObject.has("fullName"))
        assertFalse(jsonObject.has("full_name"))
        assertEquals("RMap Learner", jsonObject.get("fullName").asString)
    }

    @Test
    fun `camel case user response maps to domain user`() {
        val dto = gson.fromJson(
            """
            {
              "id": "learner",
              "email": "learner@example.com",
              "fullName": "RMap Learner",
              "avatarUrl": null,
              "role": "user",
              "createdAt": "2026-05-28T00:00:00.000Z"
            }
            """.trimIndent(),
            UserDto::class.java
        )

        val user = dto.toDomain()

        assertEquals("learner", user.id)
        assertEquals("learner@example.com", user.email)
        assertEquals("RMap Learner", user.fullName)
        assertEquals("user", user.role)
        assertEquals("2026-05-28T00:00:00.000Z", user.createdAt)
    }
}
