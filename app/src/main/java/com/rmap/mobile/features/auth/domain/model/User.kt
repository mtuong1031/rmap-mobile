package com.rmap.mobile.features.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String?,
    val role: String,
    val createdAt: String
)
