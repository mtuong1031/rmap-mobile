package com.rmap.mobile.core.utils

import android.content.Context
import com.rmap.mobile.core.network.ApiClient
import com.rmap.mobile.core.network.SessionCookieJar
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.core.storage.SharedPreferencesSessionCookieStorage
import com.rmap.mobile.core.storage.SessionCookieStorage
import com.rmap.mobile.core.database.RMapDatabase
import com.rmap.mobile.features.airoadmap.data.FakeAiRoadmapRepository
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import com.rmap.mobile.features.auth.data.remote.AuthApi
import com.rmap.mobile.features.auth.data.repository.AuthRepositoryImpl
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.GetCurrentUserUseCase
import com.rmap.mobile.features.auth.domain.usecase.LoginUseCase
import com.rmap.mobile.features.auth.domain.usecase.LogoutUseCase
import com.rmap.mobile.features.auth.domain.usecase.RegisterUseCase
import com.rmap.mobile.features.bookmarks.data.repository.RoomBookmarkRepository
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.profile.data.FakeProfileRepository
import com.rmap.mobile.features.profile.data.notification.LearningNotificationNotifier
import com.rmap.mobile.features.profile.data.notification.LearningReminderScheduler
import com.rmap.mobile.features.profile.data.notification.SharedPreferencesNotificationSettingsRepository
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.data.FakeRoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

object RMapAppGraph {
    val roadmapRepository: RoadmapRepository = FakeRoadmapRepository()
    val profileRepository: ProfileRepository = FakeProfileRepository()

    lateinit var database: RMapDatabase
        private set
    lateinit var bookmarkRepository: BookmarkRepository
        private set
    lateinit var aiRoadmapRepository: AiRoadmapRepository
        private set
    lateinit var notificationSettingsRepository: NotificationSettingsRepository
        private set
    lateinit var learningNotificationNotifier: LearningNotificationNotifier
        private set
    lateinit var sessionCookieStorage: SessionCookieStorage
        private set
    lateinit var sessionCookieJar: SessionCookieJar
        private set
    lateinit var sessionManager: SessionManager
        private set
    lateinit var apiClient: ApiClient
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var loginUseCase: LoginUseCase
        private set
    lateinit var registerUseCase: RegisterUseCase
        private set
    lateinit var logoutUseCase: LogoutUseCase
        private set
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
        private set

    fun initialize(context: Context) {
        if (::notificationSettingsRepository.isInitialized && ::authRepository.isInitialized) return

        val applicationContext = context.applicationContext
        sessionCookieStorage = SharedPreferencesSessionCookieStorage(applicationContext)
        sessionCookieJar = SessionCookieJar(sessionCookieStorage)
        sessionManager = SessionManager(
            clearSessionStorage = sessionCookieJar::clear
        )
        apiClient = ApiClient.fromBuildConfig(sessionCookieJar)
        authRepository = AuthRepositoryImpl(
            authApi = apiClient.createService(AuthApi::class.java),
            sessionManager = sessionManager
        )
        loginUseCase = LoginUseCase(authRepository)
        registerUseCase = RegisterUseCase(authRepository)
        logoutUseCase = LogoutUseCase(authRepository)
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

        database = RMapDatabase.getInstance(applicationContext)
        bookmarkRepository = RoomBookmarkRepository(
            bookmarkDao = database.bookmarkDao(),
            roadmapRepository = roadmapRepository
        )
        val scheduler = LearningReminderScheduler(applicationContext)
        aiRoadmapRepository = FakeAiRoadmapRepository(applicationContext)
        learningNotificationNotifier = LearningNotificationNotifier(applicationContext)
        learningNotificationNotifier.ensureNotificationChannel()
        notificationSettingsRepository = SharedPreferencesNotificationSettingsRepository(
            context = applicationContext,
            scheduler = scheduler
        )
    }
}
