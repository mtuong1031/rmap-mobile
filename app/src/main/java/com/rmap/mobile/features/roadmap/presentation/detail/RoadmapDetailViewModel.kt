package com.rmap.mobile.features.roadmap.presentation.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RoadmapDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RoadmapDetailUiState())
    val uiState: StateFlow<RoadmapDetailUiState> = _uiState.asStateFlow()
}
