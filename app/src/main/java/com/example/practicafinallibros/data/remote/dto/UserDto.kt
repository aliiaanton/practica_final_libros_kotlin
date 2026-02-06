package com.example.practicafinallibros.data.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)