package com.rmap.mobile.features.profile.data.model

import com.google.gson.Gson
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ProfileIdentityDtoTest {
    private val gson = Gson()

    @Test
    fun `update profile request serializes camel case fields`() {
        val json = gson.toJson(
            UpdateUserProfileRequestDto(
                fullName = "RMap Learner",
                avatarUrl = "https://api.dicebear.com/10.x/adventurer/svg?seed=learner"
            )
        )

        val jsonObject = JsonParser.parseString(json).asJsonObject

        assertEquals("RMap Learner", jsonObject.get("fullName").asString)
        assertEquals(
            "https://api.dicebear.com/10.x/adventurer/svg?seed=learner",
            jsonObject.get("avatarUrl").asString
        )
    }

    @Test
    fun `update profile request omits null avatar url`() {
        val json = gson.toJson(
            UpdateUserProfileRequestDto(
                fullName = "RMap Learner",
                avatarUrl = null
            )
        )

        assertEquals("""{"fullName":"RMap Learner"}""", json)
        assertFalse(json.contains("avatarUrl"))
    }

    @Test
    fun `profile identity response supports snake case alternates`() {
        val dto = gson.fromJson(
            """
            {
              "id": "learner",
              "email": "learner@example.com",
              "full_name": "RMap Learner",
              "avatar_url": "https://cdn.rmap.dev/avatar.svg",
              "role": "user",
              "created_at": "2026-05-28T00:00:00Z"
            }
            """.trimIndent(),
            ProfileIdentityResponseDto::class.java
        )

        assertEquals("RMap Learner", dto.fullName)
        assertEquals("https://cdn.rmap.dev/avatar.svg", dto.avatarUrl)
        assertEquals("2026-05-28T00:00:00Z", dto.createdAt)
    }
}
