package com.example.practicafinallibros.data.repository

import com.example.practicafinalLibros.data.repository.SettingsRepository
import com.example.practicafinallibros.data.remote.api.AuthApiService
import com.example.practicafinallibros.data.remote.dto.LoginRequest
import com.example.practicafinallibros.data.remote.dto.LoginResponse
import com.example.practicafinallibros.data.remote.dto.RegisterRequest

class AuthRepository(
    private val api: AuthApiService,
    private val settingsRepository: SettingsRepository
) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))

            settingsRepository.saveAuthToken(response.token)
            settingsRepository.saveUserRole(response.user.role)
            settingsRepository.saveUserId(response.user.id)
            settingsRepository.saveUserName(response.user.name)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))

            settingsRepository.saveAuthToken(response.token)
            settingsRepository.saveUserRole(response.user.role)
            settingsRepository.saveUserId(response.user.id)
            settingsRepository.saveUserName(response.user.name)

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        settingsRepository.clearUserData()
    }
}