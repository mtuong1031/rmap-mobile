package com.rmap.mobile.core.utils

import com.rmap.mobile.features.auth.data.FakeSessionRepository
import com.rmap.mobile.features.auth.domain.repository.SessionRepository
import com.rmap.mobile.features.bookmarks.data.FakeBookmarkRepository
import com.rmap.mobile.features.bookmarks.domain.repository.BookmarkRepository
import com.rmap.mobile.features.profile.data.FakeProfileRepository
import com.rmap.mobile.features.profile.domain.repository.ProfileRepository
import com.rmap.mobile.features.roadmap.data.FakeRoadmapRepository
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

object RMapAppGraph {
    val roadmapRepository: RoadmapRepository = FakeRoadmapRepository()
    val bookmarkRepository: BookmarkRepository = FakeBookmarkRepository()
    val profileRepository: ProfileRepository = FakeProfileRepository()
    val sessionRepository: SessionRepository = FakeSessionRepository()
}
