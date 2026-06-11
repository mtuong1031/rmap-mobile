package com.rmap.mobile.features.profile.presentation.viewmodel

sealed class ConnectedAccountsEvent {
    object NavigateBack : ConnectedAccountsEvent()
    data class ShowSnackbar(val message: String) : ConnectedAccountsEvent()
}
