package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrivacySecurityViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty submit validates required fields`() = runTest {
        val viewModel = newViewModel()

        viewModel.onChangePasswordClick()
        runCurrent()

        assertEquals(
            PrivacySecurityFieldError.CurrentPasswordRequired,
            viewModel.uiState.value.currentPasswordError
        )
        assertEquals(
            PrivacySecurityFieldError.NewPasswordTooShort,
            viewModel.uiState.value.newPasswordError
        )
        assertEquals(
            PrivacySecurityFieldError.ConfirmPasswordRequired,
            viewModel.uiState.value.confirmPasswordError
        )
    }

    @Test
    fun `confirm password must match new password`() = runTest {
        val viewModel = newViewModel()

        viewModel.onNewPasswordChanged("new-password")
        viewModel.onConfirmNewPasswordChanged("different-password")

        assertEquals(
            PrivacySecurityFieldError.PasswordsDoNotMatch,
            viewModel.uiState.value.confirmPasswordError
        )
        assertFalse(viewModel.uiState.value.isSubmitEnabled)
    }

    @Test
    fun `change password success calls repository and emits event`() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = newViewModel(repository = repository)
        val event = async { viewModel.events.first() }
        runCurrent()

        viewModel.onCurrentPasswordChanged("old-password")
        viewModel.onNewPasswordChanged("new-password")
        viewModel.onConfirmNewPasswordChanged("new-password")
        viewModel.onChangePasswordClick()
        runCurrent()

        assertEquals("old-password", repository.lastCurrentPassword)
        assertEquals("new-password", repository.lastNewPassword)
        assertEquals(PrivacySecurityEvent.PasswordChanged, event.await())
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("", viewModel.uiState.value.currentPassword)
    }

    @Test
    fun `change password failure surfaces error message`() = runTest {
        val viewModel = newViewModel(
            repository = FakeAuthRepository(
                result = Result.failure(IllegalStateException("Current password is incorrect."))
            )
        )

        viewModel.onCurrentPasswordChanged("old-password")
        viewModel.onNewPasswordChanged("new-password")
        viewModel.onConfirmNewPasswordChanged("new-password")
        viewModel.onChangePasswordClick()
        runCurrent()

        assertEquals("Current password is incorrect.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `password visibility toggles are independent`() = runTest {
        val viewModel = newViewModel()

        viewModel.onToggleCurrentPasswordVisibility()
        viewModel.onToggleConfirmPasswordVisibility()

        assertTrue(viewModel.uiState.value.isCurrentPasswordVisible)
        assertFalse(viewModel.uiState.value.isNewPasswordVisible)
        assertTrue(viewModel.uiState.value.isConfirmPasswordVisible)
    }

    private fun newViewModel(
        repository: FakeAuthRepository = FakeAuthRepository()
    ): PrivacySecurityViewModel {
        return PrivacySecurityViewModel(authRepository = repository)
    }

    private class FakeAuthRepository(
        private val result: Result<Unit> = Result.success(Unit)
    ) : AuthRepository {
        override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
        var lastCurrentPassword: String? = null
            private set
        var lastNewPassword: String? = null
            private set

        override suspend fun loginWithGoogle(idToken: String): Result<User> = Result.success(testUser)

        override suspend fun loginWithGithub(code: String): Result<User> = Result.success(testUser)

        override suspend fun linkWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)

        override suspend fun linkWithGithub(code: String): Result<Unit> = Result.success(Unit)

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

        override suspend fun changePassword(
            currentPassword: String,
            newPassword: String
        ): Result<Unit> {
            lastCurrentPassword = currentPassword
            lastNewPassword = newPassword
            return result
        }

        override suspend fun getCurrentUser(): Result<User> = Result.success(testUser)
    }

    private companion object {
        val testUser = User(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = null,
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
    }
}
