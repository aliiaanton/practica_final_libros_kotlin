package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.remote.UsersApi
import com.example.practicafinallibros.data.remote.dto.AuthResponse
import com.example.practicafinallibros.data.remote.dto.LoginRequest
import com.example.practicafinallibros.data.remote.dto.RegisterRequest

class AuthRepository(
    private val api: UsersApi,
    private val settingsRepository: SettingsRepository
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()!!
                settingsRepository.saveAuthToken(body.token)
                settingsRepository.saveUserRole(body.role)
                settingsRepository.saveUserId(body.userId.toString())
                settingsRepository.saveUserName(body.name)
                Result.success(body)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val registerResponse = api.register(RegisterRequest(name, email, password))

            if (registerResponse.isSuccessful) {
                // Auto-login tras registro exitoso
                login(email, password)
            } else {
                Result.failure(Exception("Error: ${registerResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        settingsRepository.clearUserData()
    }
}