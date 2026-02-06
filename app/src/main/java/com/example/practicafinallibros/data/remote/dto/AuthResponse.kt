package com.example.practicafinallibros.data.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val name: String,
    val role: String
)