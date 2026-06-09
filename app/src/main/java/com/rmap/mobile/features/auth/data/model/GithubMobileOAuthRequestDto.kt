package com.rmap.mobile.features.auth.data.model

import com.google.gson.annotations.SerializedName

data class GithubMobileOAuthRequestDto(
    @SerializedName("code") val code: String
)
