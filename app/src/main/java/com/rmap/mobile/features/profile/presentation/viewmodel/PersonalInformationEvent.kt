package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class PersonalInformationEvent {
    data object ProfileUpdated : PersonalInformationEvent()
}
