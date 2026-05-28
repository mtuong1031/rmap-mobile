package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        val normalizedEmail = email.trim()
        return when {
            !normalizedEmail.isEmailLike() -> Result.failure(IllegalArgumentException("Enter a valid email address."))
            password.length < MIN_PASSWORD_LENGTH -> Result.failure(
                IllegalArgumentException("Password must be at least 8 characters.")
            )
            else -> authRepository.login(
                email = normalizedEmail,
                password = password
            )
        }
    }
}

internal const val MIN_PASSWORD_LENGTH = 8

internal fun String.isEmailLike(): Boolean {
    return isNotBlank() && EMAIL_PATTERN.matches(this)
}

private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
