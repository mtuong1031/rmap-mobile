package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.R
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.roadmap.domain.model.LearningStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneDetail
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmission
import com.rmap.mobile.features.roadmap.domain.model.MilestoneSubmissionStatus
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestCase
import com.rmap.mobile.features.roadmap.domain.model.MilestoneTestSuite
import com.rmap.mobile.features.roadmap.domain.repository.RoadmapRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoadmapMilestoneViewModel(
    private val repository: RoadmapRepository = RMapAppGraph.roadmapRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapMilestoneUiState())
    val uiState: StateFlow<RoadmapMilestoneUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RoadmapMilestoneEvent>()
    val events: SharedFlow<RoadmapMilestoneEvent> = _events.asSharedFlow()

    fun loadMilestone(
        roadmapId: String,
        milestoneId: String,
        forceRefresh: Boolean = false
    ) {
        val normalizedRoadmapId = roadmapId.trim()
        val normalizedMilestoneId = milestoneId.trim()
        if (normalizedRoadmapId.isBlank() || normalizedMilestoneId.isBlank()) {
            _uiState.update {
                RoadmapMilestoneUiState(
                    roadmapId = normalizedRoadmapId,
                    milestoneId = normalizedMilestoneId,
                    isLoading = false,
                    errorMessageResId = R.string.roadmap_milestone_error_invalid_args
                )
            }
            return
        }

        if (
            !forceRefresh &&
            _uiState.value.roadmapId == normalizedRoadmapId &&
            _uiState.value.milestoneId == normalizedMilestoneId &&
            !_uiState.value.isLoading &&
            _uiState.value.errorMessageResId == null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    roadmapId = normalizedRoadmapId,
                    milestoneId = normalizedMilestoneId,
                    isLoading = true,
                    errorMessageResId = null,
                    repoUrlErrorResId = null
                )
            }

            repository.getMilestoneDetail(
                roadmapId = normalizedRoadmapId,
                milestoneId = normalizedMilestoneId
            ).onSuccess { detail ->
                _uiState.update { current ->
                    detail.toUiState(
                        currentRepoUrl = current.repoUrl,
                        isTestSuiteExpanded = current.isTestSuiteExpanded
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.roadmap_milestone_error_load_failed
                    )
                }
            }
        }
    }

    fun retryLoadMilestone() {
        loadMilestone(
            roadmapId = _uiState.value.roadmapId,
            milestoneId = _uiState.value.milestoneId,
            forceRefresh = true
        )
    }

    fun onRepoUrlChanged(repoUrl: String) {
        _uiState.update { state ->
            state.copy(
                repoUrl = repoUrl,
                repoUrlErrorResId = null,
                canSubmit = state.canSubmitWith(repoUrl)
            )
        }
    }

    fun onTestSuiteToggleClick() {
        _uiState.update { state ->
            state.copy(isTestSuiteExpanded = !state.isTestSuiteExpanded)
        }
    }

    fun onSubmitClick() {
        val state = _uiState.value
        if (state.isLocked) return
        if (!state.repoUrl.isValidGitHubRepoUrl()) {
            _uiState.update {
                it.copy(
                    repoUrlErrorResId = R.string.roadmap_milestone_repo_url_error,
                    canSubmit = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    repoUrlErrorResId = null,
                    errorMessageResId = null,
                    canSubmit = false
                )
            }

            repository.submitMilestone(
                roadmapId = state.roadmapId,
                milestoneId = state.milestoneId,
                repoUrl = state.repoUrl
            ).onSuccess { submission ->
                _uiState.update { current ->
                    current.copy(
                        latestSubmission = submission.toUiModel(),
                        repoUrl = submission.repoUrl,
                        isSubmitting = false,
                        canSubmit = !current.isLocked && submission.repoUrl.isValidGitHubRepoUrl()
                    )
                }
                refreshAfterSubmit()
                _events.emit(RoadmapMilestoneEvent.SubmissionQueued)
            }.onFailure {
                _uiState.update { current ->
                    current.copy(
                        isSubmitting = false,
                        canSubmit = !current.isLocked && current.repoUrl.isValidGitHubRepoUrl()
                    )
                }
                _events.emit(RoadmapMilestoneEvent.SubmissionFailed)
            }
        }
    }

    private suspend fun refreshAfterSubmit() {
        val state = _uiState.value
        repository.getMilestoneDetail(
            roadmapId = state.roadmapId,
            milestoneId = state.milestoneId
        ).onSuccess { detail ->
            _uiState.update { current ->
                detail.toUiState(
                    currentRepoUrl = current.repoUrl,
                    isTestSuiteExpanded = current.isTestSuiteExpanded
                )
            }
        }
    }
}

data class RoadmapMilestoneUiState(
    val roadmapId: String = "",
    val milestoneId: String = "",
    val title: String = "",
    val description: String? = null,
    val status: RoadmapMilestoneDetailStatusUiModel = RoadmapMilestoneDetailStatusUiModel.Locked,
    val testSuite: RoadmapMilestoneTestSuiteUiModel? = null,
    val latestSubmission: RoadmapMilestoneSubmissionUiModel? = null,
    val repoUrl: String = "",
    @param:StringRes val repoUrlErrorResId: Int? = null,
    val isTestSuiteExpanded: Boolean = false,
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = true,
    @param:StringRes val errorMessageResId: Int? = null
) {
    val isLocked: Boolean
        get() = status == RoadmapMilestoneDetailStatusUiModel.Locked
}

enum class RoadmapMilestoneDetailStatusUiModel {
    Completed,
    InProgress,
    NotStarted,
    Locked
}

data class RoadmapMilestoneTestSuiteUiModel(
    val title: String,
    val summary: String,
    val passThresholdPercent: Int,
    val testCases: List<RoadmapMilestoneTestCaseUiModel> = emptyList()
)

data class RoadmapMilestoneTestCaseUiModel(
    val name: String,
    val description: String
)

data class RoadmapMilestoneSubmissionUiModel(
    val repoUrl: String,
    val status: RoadmapMilestoneSubmissionStatusUiModel,
    val outputLog: String?,
    val passRatePercent: Int?,
    val passedTests: Int?,
    val totalTests: Int?,
    val attemptNumber: Int
) {
    val hasTestExecutionResult: Boolean
        get() = status == RoadmapMilestoneSubmissionStatusUiModel.Passed ||
            status == RoadmapMilestoneSubmissionStatusUiModel.Failed ||
            status == RoadmapMilestoneSubmissionStatusUiModel.Error
}

enum class RoadmapMilestoneSubmissionStatusUiModel {
    Running,
    Passed,
    Failed,
    Error,
    Unknown
}

sealed class RoadmapMilestoneEvent {
    data object SubmissionQueued : RoadmapMilestoneEvent()
    data object SubmissionFailed : RoadmapMilestoneEvent()
}

private fun MilestoneDetail.toUiState(
    currentRepoUrl: String,
    isTestSuiteExpanded: Boolean
): RoadmapMilestoneUiState {
    val resolvedRepoUrl = currentRepoUrl.ifBlank {
        latestSubmission?.repoUrl.orEmpty()
    }
    val statusUiModel = status.toMilestoneDetailStatusUiModel()
    return RoadmapMilestoneUiState(
        roadmapId = roadmapId,
        milestoneId = nodeId,
        title = title,
        description = description,
        status = statusUiModel,
        testSuite = testSuite?.toUiModel(),
        latestSubmission = latestSubmission?.toUiModel(),
        repoUrl = resolvedRepoUrl,
        isTestSuiteExpanded = isTestSuiteExpanded,
        canSubmit = statusUiModel != RoadmapMilestoneDetailStatusUiModel.Locked &&
            resolvedRepoUrl.isValidGitHubRepoUrl(),
        isLoading = false,
        errorMessageResId = null
    )
}

private fun MilestoneTestSuite.toUiModel(): RoadmapMilestoneTestSuiteUiModel {
    return RoadmapMilestoneTestSuiteUiModel(
        title = title,
        summary = summary,
        passThresholdPercent = passThresholdPercent,
        testCases = testCases.map { testCase -> testCase.toUiModel() }
    )
}

private fun MilestoneTestCase.toUiModel(): RoadmapMilestoneTestCaseUiModel {
    return RoadmapMilestoneTestCaseUiModel(
        name = name,
        description = description
    )
}

private fun MilestoneSubmission.toUiModel(): RoadmapMilestoneSubmissionUiModel {
    return RoadmapMilestoneSubmissionUiModel(
        repoUrl = repoUrl,
        status = status.toUiModel(),
        outputLog = outputLog,
        passRatePercent = passRatePercent,
        passedTests = passedTests,
        totalTests = totalTests,
        attemptNumber = attemptNumber
    )
}

private fun LearningStatus.toMilestoneDetailStatusUiModel(): RoadmapMilestoneDetailStatusUiModel {
    return when (this) {
        LearningStatus.Completed -> RoadmapMilestoneDetailStatusUiModel.Completed
        LearningStatus.InProgress -> RoadmapMilestoneDetailStatusUiModel.InProgress
        LearningStatus.NotStarted -> RoadmapMilestoneDetailStatusUiModel.NotStarted
        LearningStatus.Locked -> RoadmapMilestoneDetailStatusUiModel.Locked
    }
}

private fun MilestoneSubmissionStatus.toUiModel(): RoadmapMilestoneSubmissionStatusUiModel {
    return when (this) {
        MilestoneSubmissionStatus.Running -> RoadmapMilestoneSubmissionStatusUiModel.Running
        MilestoneSubmissionStatus.Passed -> RoadmapMilestoneSubmissionStatusUiModel.Passed
        MilestoneSubmissionStatus.Failed -> RoadmapMilestoneSubmissionStatusUiModel.Failed
        MilestoneSubmissionStatus.Error -> RoadmapMilestoneSubmissionStatusUiModel.Error
        MilestoneSubmissionStatus.Unknown -> RoadmapMilestoneSubmissionStatusUiModel.Unknown
    }
}

private fun RoadmapMilestoneUiState.canSubmitWith(repoUrl: String): Boolean {
    return !isLocked && !isLoading && !isSubmitting && repoUrl.isValidGitHubRepoUrl()
}

private fun String.isValidGitHubRepoUrl(): Boolean {
    return trim().startsWith("https://github.com/") && trim().length > "https://github.com/".length
}
