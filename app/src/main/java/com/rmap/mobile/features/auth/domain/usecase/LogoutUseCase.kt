package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.core.database.sync.ClearDynamicDataUseCase
import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val clearDynamicDataUseCase: ClearDynamicDataUseCase? = null
) {
    suspend operator fun invoke(): Result<Unit> {
        clearDynamicDataUseCase?.invoke()
        return authRepository.logout()
    }
}
