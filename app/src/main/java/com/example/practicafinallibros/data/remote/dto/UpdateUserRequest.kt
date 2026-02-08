package com.example.practicafinallibros.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String
)
