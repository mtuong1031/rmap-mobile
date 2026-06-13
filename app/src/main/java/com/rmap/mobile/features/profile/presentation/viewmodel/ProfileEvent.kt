package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class ProfileEvent {
    data object NavigateToPersonalInformation : ProfileEvent()
    data object NavigateToNotificationSettings : ProfileEvent()
    data object NavigateToPrivacySecurity : ProfileEvent()
    data object NavigateToConnectedAccounts : ProfileEvent()
    data object ShowComingSoon : ProfileEvent()
    data object SignedOut : ProfileEvent()
    data object NavigateToAuth : ProfileEvent()
}
