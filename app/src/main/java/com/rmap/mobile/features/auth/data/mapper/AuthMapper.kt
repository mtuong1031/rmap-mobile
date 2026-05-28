package com.rmap.mobile.features.auth.data.mapper

import com.rmap.mobile.features.auth.data.model.UserDto
import com.rmap.mobile.features.auth.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        fullName = fullName,
        avatarUrl = avatarUrl,
        role = role,
        createdAt = createdAt
    )
}
