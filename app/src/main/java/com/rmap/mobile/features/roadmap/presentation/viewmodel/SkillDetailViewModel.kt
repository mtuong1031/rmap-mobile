package com.rmap.mobile.features.roadmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmap.mobile.core.utils.RMapAppGraph
import com.rmap.mobile.features.roadmap.domain.model.SkillDetail
import com.rmap.mobile.features.roadmap.domain.repository.SkillLearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SkillDetailViewModel(
    private val repository: SkillLearningRepository = RMapAppGraph.skillLearningRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LearningNodeUiState())
    val uiState: StateFlow<LearningNodeUiState> = _uiState.asStateFlow()

    private var currentSkillId = ""

    fun loadSkill(
        skillId: String,
        forceRefresh: Boolean = false
    ) {
        val normalizedSkillId = skillId.trim()
        if (!forceRefresh && normalizedSkillId == currentSkillId && !_uiState.value.isLoading) {
            return
        }
        currentSkillId = normalizedSkillId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            repository.getSkillDetail(normalizedSkillId)
                .onSuccess { skill ->
                    _uiState.update { skill.toUiState() }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun retry() {
        loadSkill(currentSkillId, forceRefresh = true)
    }
}

private fun SkillDetail.toUiState(): LearningNodeUiState {
    return LearningNodeUiState(
        nodeId = id,
        title = name,
        description = description,
        skillName = category,
        estimatedHours = estimatedHours,
        status = LearningNodeStatusUiModel.NotStarted,
        requirement = RoadmapNodeRequirement.Optional,
        resources = emptyList(),
        prerequisites = emptyList(),
        isQuizAvailable = false,
        isLoading = false,
        errorMessage = null
    )
}
