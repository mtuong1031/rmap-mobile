package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
