package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String
    ): Result<User> {
        val normalizedEmail = email.trim()
        val normalizedFullName = fullName.trim()
        return when {
            normalizedFullName.isBlank() -> Result.failure(IllegalArgumentException("Enter your full name."))
            !normalizedEmail.isEmailLike() -> Result.failure(IllegalArgumentException("Enter a valid email address."))
            password.length < MIN_PASSWORD_LENGTH -> Result.failure(
                IllegalArgumentException("Password must be at least 8 characters.")
            )
            else -> authRepository.register(
                email = normalizedEmail,
                password = password,
                fullName = normalizedFullName
            )
        }
    }
}
