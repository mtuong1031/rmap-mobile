package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class ProfileEvent {
    data object NavigateToAuthentication : ProfileEvent()
    data object NavigateToExplore : ProfileEvent()
    data object NavigateToNotificationSettings : ProfileEvent()
    data object ShowComingSoon : ProfileEvent()
    data object SignedOut : ProfileEvent()
}
