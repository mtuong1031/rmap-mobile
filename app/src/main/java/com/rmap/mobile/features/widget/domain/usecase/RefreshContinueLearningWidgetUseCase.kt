package com.rmap.mobile.features.widget.domain.usecase

import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.widget.domain.model.ContinueLearningWidgetSnapshot

class RefreshContinueLearningWidgetUseCase(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val updateWidget: UpdateContinueLearningWidgetUseCase
) {
    suspend operator fun invoke(): Result<ContinueLearningWidgetSnapshot> {
        val authState = authRepository.authState.value
        if (authState !is AuthState.Authenticated) {
            return updateWidget(authState = authState, homeContent = null)
        }

        return homeRepository.getHomeContent().fold(
            onSuccess = { content ->
                updateWidget(
                    authState = authState,
                    homeContent = content
                )
            },
            onFailure = Result.Companion::failure
        )
    }
}
