package com.rmap.mobile.core.auth

sealed interface PendingProtectedAction {
    data object GenerateAiRoadmap : PendingProtectedAction
}
