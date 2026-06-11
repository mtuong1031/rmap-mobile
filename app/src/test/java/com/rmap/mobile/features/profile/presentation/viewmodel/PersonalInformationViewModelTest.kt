package com.rmap.mobile.features.profile.presentation.viewmodel

import com.rmap.mobile.MainDispatcherRule
import com.rmap.mobile.features.auth.domain.model.AuthState
import com.rmap.mobile.features.auth.domain.model.User
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.profile.domain.model.UserActivitySummary
import com.rmap.mobile.features.profile.domain.model.UserProfileIdentity
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalInformationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initializes form from authenticated user`() = runTest {
        val viewModel = newViewModel()

        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("RMap Learner", viewModel.uiState.value.fullName)
        assertEquals(testUser.avatarUrl.orEmpty(), viewModel.uiState.value.avatarUrl)
        assertEquals(32, viewModel.uiState.value.avatarSeeds.size)
    }

    @Test
    fun `validates full name length and disables save`() = runTest {
        val viewModel = newViewModel()
        runCurrent()

        viewModel.onStartEditingDetails()
        viewModel.onFullNameChanged("A")

        assertEquals(PersonalInformationFieldError.NameTooShort, viewModel.uiState.value.fieldError)
        assertFalse(viewModel.uiState.value.isSaveEnabled)
    }

    @Test
    fun `selecting avatar marks form dirty`() = runTest {
        val viewModel = newViewModel()
        runCurrent()

        viewModel.onOpenAvatarPicker()
        viewModel.onAvatarSelected("new-avatar")

        assertTrue(viewModel.uiState.value.isDirty)
        assertTrue(viewModel.uiState.value.avatarUrl.contains("new-avatar"))
    }

    @Test
    fun `save updates profile and refreshes current user`() = runTest {
        val repository = FakeProfileRepository()
        val authRepository = FakeAuthRepository()
        val viewModel = newViewModel(
            repository = repository,
            authRepository = authRepository
        )
        runCurrent()

        viewModel.onStartEditingDetails()
        viewModel.onFullNameChanged(" Updated Learner ")
        viewModel.onSaveClick()
        runCurrent()

        assertEquals("Updated Learner", repository.lastFullName)
        assertEquals(1, authRepository.getCurrentUserCalls)
        assertFalse(viewModel.uiState.value.isSaving)
        assertFalse(viewModel.uiState.value.isDirty)
        assertEquals("Updated Learner", viewModel.uiState.value.fullName)
    }

    @Test
    fun `save exposes failure message`() = runTest {
        val viewModel = newViewModel(
            repository = FakeProfileRepository(error = IllegalStateException("Profile update failed"))
        )
        runCurrent()

        viewModel.onFullNameChanged("Updated Learner")
        viewModel.onSaveClick()
        runCurrent()

        assertEquals("Profile update failed", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    private fun newViewModel(
        repository: FakeProfileRepository = FakeProfileRepository(),
        authRepository: FakeAuthRepository = FakeAuthRepository()
    ): PersonalInformationViewModel {
        return PersonalInformationViewModel(
            profileRepository = repository,
            authRepository = authRepository
        )
    }

    private class FakeProfileRepository(
        private val error: Throwable? = null
    ) : ProfileRepository {
        var lastFullName: String? = null
            private set
        var lastAvatarUrl: String? = null
            private set

        override suspend fun getActivity(): Result<UserActivitySummary> {
            return Result.success(UserActivitySummary(streakDays = 0, longestStreak = 0))
        }

        override suspend fun updateProfile(
            fullName: String,
            avatarUrl: String
        ): Result<UserProfileIdentity> {
            lastFullName = fullName
            lastAvatarUrl = avatarUrl
            return error?.let { Result.failure(it) } ?: Result.success(
                UserProfileIdentity(
                    id = testUser.id,
                    email = testUser.email,
                    fullName = fullName,
                    avatarUrl = avatarUrl,
                    role = testUser.role,
                    createdAt = testUser.createdAt
                )
            )
        }
    }

    private class FakeAuthRepository : AuthRepository {
        private val mutableAuthState = MutableStateFlow<AuthState>(AuthState.Authenticated(testUser))
        override val authState: StateFlow<AuthState> = mutableAuthState
        var getCurrentUserCalls: Int = 0
            private set

        override suspend fun loginWithGoogle(idToken: String): Result<User> = Result.success(testUser)

        override suspend fun loginWithGithub(code: String): Result<User> = Result.success(testUser)

        override suspend fun logout(): Result<Unit> = Result.success(Unit)

        override suspend fun changePassword(
            currentPassword: String,
            newPassword: String
        ): Result<Unit> = Result.success(Unit)

        override suspend fun getCurrentUser(): Result<User> {
            getCurrentUserCalls += 1
            return Result.success(testUser)
        }
    }

    private companion object {
        val testUser = User(
            id = "learner",
            email = "learner@example.com",
            fullName = "RMap Learner",
            avatarUrl = "https://api.dicebear.com/10.x/adventurer/svg?seed=learner",
            role = "user",
            createdAt = "2026-05-28T00:00:00Z"
        )
    }
}
