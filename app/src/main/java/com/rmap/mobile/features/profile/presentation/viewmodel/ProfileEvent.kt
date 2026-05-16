package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class ProfileEvent {
    data object ShowComingSoon : ProfileEvent()
    data object SignedOut : ProfileEvent()
}
