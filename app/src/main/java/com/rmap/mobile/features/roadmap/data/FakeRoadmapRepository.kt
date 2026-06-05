package com.rmap.mobile.features.roadmap.data

import com.rmap.mobile.features.roadmap.domain.model.AiScholarTip
import com.rmap.mobile.features.roadmap.domain.model.LearningDifficulty
import com.rmap.mobile.features.roadmap.domain.model.LearningModule
import com.rmap.mobile.features.roadmap.domain.model.LearningModuleSection
import com.rmap.mobile.features.roadmap.domain.model.LearningNodeDetail
import com.rmap.mobile.features.roadmap.domain.model.LearningPrerequisite
import com.rmap.mobile.features.roadmap.domain.model.LearningProgress
import com.rmap.mobile.features.roadmap.domain.model.LearningRequirement
import com.rmap.mobile.features.roadmap.domain.model.LearningResource
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.LearningTopicIcon
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestCase
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuite
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuiteStatus
import com.rmap.mobile.features.roadmap.domain.model.NodeQuiz
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizAnswer
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizOption
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestion
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizQuestionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeQuizSubmissionResult
import com.rmap.mobile.features.roadmap.domain.model.NodeProgressUpdateResult
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCategory
import com.rmap.mobile.features.roadmap.domain.model.RoadmapCoverPlaceholder
import com.rmap.mobile.features.roadmap.domain.model.RoadmapDetail
import com.rmap.mobile.features.roadmap.domain.model.RoadmapMilestone
import com.rmap.mobile.features.roadmap.domain.model.RoadmapSummary
import com.rmap.mobile.features.roadmap.domain.model.SkillLearningContent
import com.rmap.mobile.features.roadmap.domain.model.SubLesson
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository

class FakeRoadmapRepository : RoadmapRepository {
    private val categories = listOf(
        RoadmapCategory("frontend", "Frontend", LearningTopicIcon.Code),
        RoadmapCategory("backend", "Backend", LearningTopicIcon.Storage),
        RoadmapCategory("mobile", "Mobile", LearningTopicIcon.Devices),
        RoadmapCategory("devops", "DevOps", LearningTopicIcon.Terminal),
        RoadmapCategory("ai", "AI", LearningTopicIcon.SmartToy)
    )

    private val roadmaps = listOf(
        RoadmapSummary("frontend-pro", "Frontend Pro", 120, 1, LearningDifficulty.Expert, "3 months", LearningTopicIcon.Code, "frontend", "MOST POPULAR", 48),
        RoadmapSummary("devops-specialist", "DevOps Specialist", 185, 0, LearningDifficulty.Beginner, "6 months", LearningTopicIcon.DataObject, "devops"),
        RoadmapSummary("ui-ux-master", "UI/UX Master", 96, 0, LearningDifficulty.Intermediate, "2 months", LearningTopicIcon.Palette, "frontend"),
        RoadmapSummary("data-science", "Data Science", 240, 0, LearningDifficulty.Hard, "4 months", LearningTopicIcon.Science, "ai"),
        RoadmapSummary("ai-engineering", "AI Engineering Path", 64, 0, LearningDifficulty.Advanced, "4 months", LearningTopicIcon.SmartToy, "ai", "CAREER PATH", 64),
        RoadmapSummary("full-stack-development", "Full Stack Development", 160, 24, LearningDifficulty.Intermediate, "8 months", LearningTopicIcon.Code, "backend", coverPlaceholder = RoadmapCoverPlaceholder.FullStack),
        RoadmapSummary("ui-ux-masterclass", "UI/UX Masterclass", 72, 0, LearningDifficulty.Beginner, "4 months", LearningTopicIcon.Palette, "frontend", coverPlaceholder = RoadmapCoverPlaceholder.UiUx),
        RoadmapSummary("android-foundations", "Android Foundations", 88, 0, LearningDifficulty.Beginner, "3 months", LearningTopicIcon.Devices, "mobile"),
        RoadmapSummary("ios-swift-starter", "iOS Swift Starter", 76, 0, LearningDifficulty.Beginner, "3 months", LearningTopicIcon.Devices, "mobile"),
        RoadmapSummary("backend-api-design", "Backend API Design", 112, 0, LearningDifficulty.Intermediate, "4 months", LearningTopicIcon.Storage, "backend"),
        RoadmapSummary("kotlin-server-side", "Kotlin Server Side", 98, 0, LearningDifficulty.Intermediate, "4 months", LearningTopicIcon.Code, "backend"),
        RoadmapSummary("cloud-devops", "Cloud DevOps", 132, 0, LearningDifficulty.Advanced, "5 months", LearningTopicIcon.Terminal, "devops"),
        RoadmapSummary("ci-cd-automation", "CI/CD Automation", 84, 0, LearningDifficulty.Intermediate, "2 months", LearningTopicIcon.Terminal, "devops"),
        RoadmapSummary("machine-learning-basics", "Machine Learning Basics", 128, 0, LearningDifficulty.Intermediate, "5 months", LearningTopicIcon.Science, "ai"),
        RoadmapSummary("prompt-engineering", "Prompt Engineering", 54, 0, LearningDifficulty.Beginner, "1 month", LearningTopicIcon.SmartToy, "ai")
    )

    private val details = mapOf(
        "frontend-pro" to RoadmapDetail(
            id = "frontend-pro",
            title = "Frontend Pro",
            categoryLabel = "Web Development",
            completedLessons = 6,
            totalLessons = 8,
            sections = listOf(
                LearningModuleSection(
                    title = "Core Web Fundamentals",
                    modules = listOf(
                        LearningModule(
                            id = "node-html-css",
                            title = "HTML & CSS",
                            status = LearningStatus.Completed,
                            progressPercent = 100,
                            icon = LearningTopicIcon.Code,
                            subLessons = listOf(
                                SubLesson("Semantic HTML", LearningStatus.Completed),
                                SubLesson("CSS Flexbox & Grid", LearningStatus.Completed),
                                SubLesson("Responsive Design", LearningStatus.Completed)
                            )
                        ),
                        LearningModule(
                            id = "node-javascript-basics",
                            title = "JavaScript Basics",
                            status = LearningStatus.InProgress,
                            progressPercent = 45,
                            icon = LearningTopicIcon.DataObject,
                            subLessons = listOf(
                                SubLesson("ES6+ Syntax", LearningStatus.Completed),
                                SubLesson("Asynchronous JS", LearningStatus.InProgress),
                                SubLesson("DOM Manipulation", LearningStatus.Locked)
                            )
                        )
                    )
                ),
                LearningModuleSection(
                    title = "Framework Ecosystem",
                    modules = listOf(
                        LearningModule(
                            id = "node-react-fundamentals",
                            title = "React Fundamentals",
                            status = LearningStatus.Locked,
                            progressPercent = 0,
                            icon = LearningTopicIcon.Storage,
                            subLessons = emptyList()
                        )
                    )
                )
            ),
            milestones = listOf(
                RoadmapMilestone(
                    id = "milestone-static-page",
                    title = "Static Web Page",
                    description = "Build a responsive multi-page static website.",
                    status = LearningStatus.Locked
                )
            ),
            aiTip = AiScholarTip("Asynchronous JS", "Promises", "DOM Manipulation")
        )
    )

    override suspend fun getLearningProgress(): Result<LearningProgress> = Result.success(
        LearningProgress(
            completedLessons = 1,
            totalLessons = 107,
            streakDays = 2,
            todayGoalCompleted = 1,
            todayGoalTotal = 3,
            completedRoadmaps = 1
        )
    )

    override suspend fun getTrendingRoadmaps(): Result<List<RoadmapSummary>> = Result.success(roadmaps.take(4))

    override suspend fun getExploreCategories(): Result<List<RoadmapCategory>> = Result.success(categories)

    override suspend fun getRecommendedRoadmaps(): Result<List<RoadmapSummary>> =
        Result.success(roadmaps.filter { it.recommendationBadge != null })

    override suspend fun searchRoadmaps(query: String): Result<List<RoadmapSummary>> {
        val normalizedQuery = query.trim()
        val result = if (normalizedQuery.isBlank()) {
            roadmaps
        } else {
            roadmaps.filter { roadmap ->
                roadmap.title.contains(normalizedQuery, ignoreCase = true) ||
                    roadmap.categoryId.contains(normalizedQuery, ignoreCase = true)
            }
        }
        return Result.success(result)
    }

    override suspend fun getRoadmapDetail(id: String): Result<RoadmapDetail> {
        return details[id]?.let { detail -> Result.success(detail) }
            ?: Result.failure(IllegalArgumentException("Roadmap not found"))
    }

    override suspend fun getLearningNode(
        roadmapId: String,
        nodeId: String
    ): Result<LearningNodeDetail> {
        val module = details[roadmapId]
            ?.sections
            ?.flatMap { section -> section.modules }
            ?.firstOrNull { candidate -> candidate.id == nodeId || candidate.title == nodeId }
            ?: return Result.failure(IllegalArgumentException("Node not found"))

        return Result.success(
            LearningNodeDetail(
                roadmapId = roadmapId,
                nodeId = module.id,
                title = module.title,
                description = module.description ?: "Learn the core concept, practice it, then validate your readiness with a short quiz.",
                skillName = module.title,
                skillDescription = module.description,
                estimatedHours = 2,
                status = module.status,
                requirement = module.requirement,
                resources = listOf(
                    LearningResource(
                        id = "mdn-${module.id}",
                        title = "MDN Web Docs",
                        url = "https://developer.mozilla.org/",
                        type = "ARTICLE",
                        isFree = true,
                        isPrimary = true
                    ),
                    LearningResource(
                        id = "practice-${module.id}",
                        title = "Hands-on practice",
                        url = "https://roadmap.sh/",
                        type = "PRACTICE",
                        isFree = true,
                        isPrimary = false
                    )
                ),
                prerequisites = listOf(
                    LearningPrerequisite(
                        skillId = "node-html-css",
                        skillName = "HTML & CSS"
                    )
                )
            )
        )
    }

    override suspend fun getMilestoneDetail(
        roadmapId: String,
        milestoneId: String
    ): Result<MilestoneDetail> {
        val milestone = details[roadmapId]
            ?.milestones
            ?.firstOrNull { candidate -> candidate.id == milestoneId }
            ?: return Result.failure(IllegalArgumentException("Milestone not found"))

        return Result.success(
            MilestoneDetail(
                roadmapId = roadmapId,
                nodeId = milestone.id,
                title = milestone.title,
                description = milestone.description,
                status = milestone.status,
                testSuite = MilestoneTestSuite(
                    id = "suite-${milestone.id}",
                    title = "${milestone.title} Evaluation",
                    summary = "Submit a GitHub repository and RMap will run the milestone test suite.",
                    passThresholdPercent = 80,
                    status = MilestoneTestSuiteStatus.Ready,
                    testCases = listOf(
                        MilestoneTestCase(
                            name = "Project structure",
                            description = "Validate the repository contains the expected source files."
                        )
                    )
                ),
                latestSubmission = null
            )
        )
    }

    override suspend fun submitMilestone(
        roadmapId: String,
        milestoneId: String,
        repoUrl: String
    ): Result<MilestoneSubmission> {
        return Result.success(
            MilestoneSubmission(
                id = "submission-$milestoneId",
                repoUrl = repoUrl,
                testSuiteId = "suite-$milestoneId",
                status = MilestoneSubmissionStatus.Running,
                outputLog = null,
                passRatePercent = null,
                passedTests = null,
                totalTests = null,
                attemptNumber = 1,
                createdAt = "2026-06-01T00:00:00Z",
                completedAt = null,
                testResults = emptyList()
            )
        )
    }

    override suspend fun getNodeQuiz(
        roadmapId: String,
        nodeId: String
    ): Result<NodeQuiz> {
        return Result.success(
            NodeQuiz(
                nodeId = nodeId,
                skillId = nodeId,
                questions = listOf(
                    NodeQuizQuestion(
                        id = "question-1",
                        text = "Which option best describes a learning checkpoint?",
                        options = quizOptions()
                    ),
                    NodeQuizQuestion(
                        id = "question-2",
                        text = "What should you do before moving to the next node?",
                        options = quizOptions()
                    )
                )
            )
        )
    }

    override suspend fun submitNodeQuiz(
        roadmapId: String,
        nodeId: String,
        answers: List<NodeQuizAnswer>
    ): Result<NodeQuizSubmissionResult> {
        return Result.success(
            NodeQuizSubmissionResult(
                scorePercent = 80,
                passed = true,
                correctCount = answers.size,
                totalQuestions = answers.size,
                suggestion = null,
                unlockedNodeIds = emptyList(),
                questionResults = answers.map { answer ->
                    NodeQuizQuestionResult(
                        questionId = answer.questionId,
                        selectedOption = answer.selectedOption,
                        correctOption = answer.selectedOption,
                        isCorrect = true
                    )
                }
            )
        )
    }

    private fun quizOptions(): List<NodeQuizOption> {
        return listOf(
            NodeQuizOption("A", "Review the concept and apply it in a small task."),
            NodeQuizOption("B", "Skip practice and continue immediately."),
            NodeQuizOption("C", "Only bookmark the resource."),
            NodeQuizOption("D", "Wait until every roadmap is finished.")
        )
    }

    override suspend fun getRoadmapNodeLearningContent(
        roadmapId: String,
        nodeId: String,
        skillId: String
    ): Result<SkillLearningContent> {
        return Result.failure(IllegalArgumentException("Roadmap node not found"))
    }

    override suspend fun startRoadmap(roadmapId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateNodeProgress(
        roadmapId: String,
        nodeId: String,
        status: LearningStatus
    ): Result<NodeProgressUpdateResult> {
        return Result.success(
            NodeProgressUpdateResult(
                nodeId = nodeId,
                status = status,
                unlockedNodeIds = emptyList()
            )
        )
    }
}
