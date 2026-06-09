package com.rmap.mobile.features.auth.domain.usecase

import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository

class LoginWithGoogleUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.loginWithGoogle(idToken)
    }
}
