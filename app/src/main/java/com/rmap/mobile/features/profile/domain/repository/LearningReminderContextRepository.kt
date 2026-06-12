package com.rmap.mobile.features.profile.domain.repository

import com.rmap.mobile.features.profile.domain.model.LearningReminderContext

interface LearningReminderContextRepository {
    fun getContext(): LearningReminderContext

    suspend fun setActiveRoadmap(title: String?)
}
