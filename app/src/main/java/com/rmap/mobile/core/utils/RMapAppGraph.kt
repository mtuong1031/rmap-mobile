package com.rmap.mobile.core.utils

import android.content.Context
import com.rmap.mobile.features.airoadmap.data.FakeAiRoadmapRepository
import com.rmap.mobile.features.airoadmap.domain.repository.AiRoadmapRepository
import com.rmap.mobile.features.auth.data.FakeSessionRepository
import com.rmap.mobile.features.auth.domain.repository.SessionRepository
import com.rmap.mobile.features.bookmarks.data.FakeBookmarkRepository
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.profile.data.notification.LearningNotificationNotifier
import com.rmap.mobile.features.profile.data.notification.LearningReminderScheduler
import com.rmap.mobile.features.profile.data.FakeProfileRepository
import com.rmap.mobile.features.profile.data.notification.SharedPreferencesNotificationSettingsRepository
import com.rmap.mobile.features.profile.domain.repository.NotificationSettingsRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.data.FakeRoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

object RMapAppGraph {
    val roadmapRepository: RoadmapRepository = FakeRoadmapRepository()
    val bookmarkRepository: BookmarkRepository = FakeBookmarkRepository()
    val profileRepository: ProfileRepository = FakeProfileRepository()
    val sessionRepository: SessionRepository = FakeSessionRepository()

    lateinit var aiRoadmapRepository: AiRoadmapRepository
        private set
    lateinit var notificationSettingsRepository: NotificationSettingsRepository
        private set
    lateinit var learningNotificationNotifier: LearningNotificationNotifier
        private set

    fun initialize(context: Context) {
        if (::notificationSettingsRepository.isInitialized) return

        val applicationContext = context.applicationContext
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
