package com.rmap.mobile.core.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.rmap.mobile.core.database.DatabaseProvider
import com.rmap.mobile.core.database.RMapDatabase
import com.rmap.mobile.core.database.sync.ClearDynamicDataUseCase
import com.rmap.mobile.core.database.sync.SyncApi
import com.rmap.mobile.core.database.sync.SyncManager
import com.rmap.mobile.core.network.ApiClient
import com.rmap.mobile.core.network.SessionCookieJar
import com.rmap.mobile.core.session.SessionManager
import com.rmap.mobile.core.storage.SharedPreferencesSessionCookieStorage
import com.rmap.mobile.core.storage.SessionCookieStorage
import com.rmap.mobile.features.airoadmap.data.remote.AiRoadmapApi
import com.rmap.mobile.features.airoadmap.data.repository.RemoteAiRoadmapRepository
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import com.rmap.mobile.features.auth.data.remote.AuthApi
import com.rmap.mobile.features.auth.data.repository.AuthRepositoryImpl
import com.rmap.mobile.features.auth.domain.repository.AuthRepository
import com.rmap.mobile.features.auth.domain.usecase.GetCurrentUserUseCase
import com.rmap.mobile.features.auth.domain.usecase.LoginWithGoogleUseCase
import com.rmap.mobile.features.auth.domain.usecase.LoginWithGithubUseCase
import com.rmap.mobile.features.auth.domain.usecase.LogoutUseCase
import com.rmap.mobile.features.dashboard.data.remote.DashboardApi
import com.rmap.mobile.features.dashboard.data.repository.DashboardRepositoryImpl
import com.rmap.mobile.features.dashboard.domain.repository.DashboardRepository
import com.rmap.mobile.features.home.data.remote.HomeApi
import com.rmap.mobile.features.home.data.repository.HomeRepositoryImpl
import com.rmap.mobile.features.home.data.repository.SharedPreferencesRecentSearchRepository
import com.rmap.mobile.features.home.domain.repository.HomeRepository
import com.rmap.mobile.features.home.domain.repository.RecentSearchRepository
import com.rmap.mobile.features.profile.data.notification.LearningNotificationNotifier
import com.rmap.mobile.features.profile.data.notification.LearningReminderScheduler
import com.rmap.mobile.features.profile.data.notification.SharedPreferencesNotificationSettingsRepository
import com.rmap.mobile.features.profile.data.remote.ProfileApi
import com.rmap.mobile.features.profile.data.repository.ProfileRepositoryImpl
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.data.remote.api.RoadmapApi
import com.rmap.mobile.features.roadmap.data.remote.api.SkillApi
import com.rmap.mobile.features.roadmap.data.repository.RemoteRoadmapRepository
import com.rmap.mobile.features.roadmap.data.repository.RemoteSkillLearningRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository

/**
 * Manual Service Locator for the application.
 *
 * NOTE: Fields holding objects that contain an [android.app.Application] context are safe from 
 * memory leaks as both live for the entire process duration.
 */
object RMapAppGraph {
    lateinit var roadmapRepository: RoadmapRepository
        private set
    lateinit var profileRepository: ProfileRepository
        private set
    lateinit var dashboardRepository: DashboardRepository
        private set
    lateinit var skillLearningRepository: SkillLearningRepository
        private set
    lateinit var aiRoadmapRepository: AiRoadmapRepository
        private set
    lateinit var notificationSettingsRepository: NotificationSettingsRepository
        private set
    @get:SuppressLint("StaticFieldLeak")
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
    lateinit var database: RMapDatabase
        private set
    lateinit var syncManager: SyncManager
        private set
    lateinit var clearDynamicDataUseCase: ClearDynamicDataUseCase
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var homeRepository: HomeRepository
        private set
    lateinit var recentSearchRepository: RecentSearchRepository
        private set

    lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase
        private set
    lateinit var loginWithGithubUseCase: LoginWithGithubUseCase
        private set

    lateinit var logoutUseCase: LogoutUseCase
        private set
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
        private set

    fun initialize(context: Context) {
        if (
            ::notificationSettingsRepository.isInitialized &&
            ::authRepository.isInitialized &&
            ::roadmapRepository.isInitialized &&
            ::dashboardRepository.isInitialized &&
            ::profileRepository.isInitialized &&
            ::recentSearchRepository.isInitialized
        ) {
            return
        }

        val applicationContext = context.applicationContext
        val application = applicationContext as Application
        sessionCookieStorage = SharedPreferencesSessionCookieStorage(applicationContext)
        sessionCookieJar = SessionCookieJar(sessionCookieStorage)
        sessionManager = SessionManager(
            clearSessionStorage = sessionCookieJar::clear
        )
        apiClient = ApiClient.fromBuildConfig(sessionCookieJar, sessionManager)
        database = DatabaseProvider.getDatabase(applicationContext)
        syncManager = SyncManager(
            syncApi = apiClient.createService(SyncApi::class.java),
            syncMetadataDao = database.syncMetadataDao()
        )
        clearDynamicDataUseCase = ClearDynamicDataUseCase(database)
        authRepository = AuthRepositoryImpl(
            authApi = apiClient.createService(AuthApi::class.java),
            sessionManager = sessionManager
        )
        homeRepository = HomeRepositoryImpl(
            homeApi = apiClient.createService(HomeApi::class.java),
            templateCategoryDao = database.templateCategoryDao(),
            trendingRoadmapDao = database.homeTrendingRoadmapDao(),
            syncManager = syncManager
        )
        recentSearchRepository = SharedPreferencesRecentSearchRepository(applicationContext)
        profileRepository = ProfileRepositoryImpl(
            profileApi = apiClient.createService(ProfileApi::class.java),
            sessionManager = sessionManager
        )
        dashboardRepository = DashboardRepositoryImpl(
            dashboardApi = apiClient.createService(DashboardApi::class.java),
            sessionManager = sessionManager,
            dashboardCacheDao = database.dashboardCacheDao()
        )
//        roadmapRepository = RoadmapRepositoryImpl(
//            roadmapApi = apiClient.createService(RoadmapApi::class.java),
//            sessionManager = sessionManager
//        )

        loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
        loginWithGithubUseCase = LoginWithGithubUseCase(authRepository)
        logoutUseCase = LogoutUseCase(
            authRepository = authRepository,
            clearDynamicDataUseCase = clearDynamicDataUseCase
        )
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
        roadmapRepository = RemoteRoadmapRepository(
            roadmapApi = apiClient.createService(RoadmapApi::class.java),
            sessionManager = sessionManager,
            templateCategoryDao = database.templateCategoryDao(),
            templateRoadmapDao = database.templateRoadmapDao(),
            syncManager = syncManager,
            roadmapDetailCacheDao = database.roadmapDetailCacheDao()
        )
        skillLearningRepository = RemoteSkillLearningRepository(
            skillApi = apiClient.createService(SkillApi::class.java),
            sessionManager = sessionManager,
            skillDao = database.skillDao(),
            syncManager = syncManager
        )

        val scheduler = LearningReminderScheduler(applicationContext)
        aiRoadmapRepository = RemoteAiRoadmapRepository(
            context = applicationContext,
            api = apiClient.createService(AiRoadmapApi::class.java),
            sessionManager = sessionManager,
            aiRoadmapCacheDao = database.aiRoadmapCacheDao()
        )
        learningNotificationNotifier = LearningNotificationNotifier(application)
        learningNotificationNotifier.ensureNotificationChannel()
        notificationSettingsRepository = SharedPreferencesNotificationSettingsRepository(
            context = applicationContext,
            scheduler = scheduler
        )
    }
}
