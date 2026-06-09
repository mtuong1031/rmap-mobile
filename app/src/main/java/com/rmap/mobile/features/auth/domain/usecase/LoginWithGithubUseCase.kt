package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class LoginWithGithubUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<User> {
        return authRepository.loginWithGithub(code)
    }
}
