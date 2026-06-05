package com.rmap.mobile.features.roadmap.domain.model

data class MilestoneDetail(
    val roadmapId: String,
    val nodeId: String,
    val title: String,
    val description: String?,
    val status: LearningStatus,
    val testSuite: MilestoneTestSuite?,
    val latestSubmission: MilestoneSubmission?
)

data class MilestoneTestSuite(
    val id: String,
    val title: String,
    val summary: String,
    val passThresholdPercent: Int,
    val status: MilestoneTestSuiteStatus,
    val testCases: List<MilestoneTestCase>
)

data class MilestoneTestCase(
    val name: String,
    val description: String
)

enum class MilestoneTestSuiteStatus {
    Ready,
    Generating,
    Failed,
    NotGenerated,
    Unknown
}

data class MilestoneSubmission(
    val id: String,
    val repoUrl: String,
    val testSuiteId: String?,
    val status: MilestoneSubmissionStatus,
    val outputLog: String?,
    val passRatePercent: Int?,
    val passedTests: Int?,
    val totalTests: Int?,
    val attemptNumber: Int,
    val createdAt: String,
    val completedAt: String?,
    val testResults: List<MilestoneSubmissionTestResult>
)

data class MilestoneSubmissionTestResult(
    val name: String,
    val message: String,
    val passed: Boolean
)

enum class MilestoneSubmissionStatus {
    Running,
    Passed,
    Failed,
    Error,
    Unknown
}
