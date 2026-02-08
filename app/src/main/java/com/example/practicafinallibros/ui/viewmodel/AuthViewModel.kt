package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.repository.AuthRepository
import com.example.practicafinallibros.data.repository.SettingsRepository
import com.example.practicafinallibros.ui.state.AuthUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var token by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    var userName by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            settingsRepository.getAuthToken().collectLatest {
                savedToken -> token = savedToken
                isLoggedIn = savedToken != null
            }
        }
        viewModelScope.launch {
            settingsRepository.isUserAdmin().collectLatest {
                admin -> isAdmin = admin
            }
        }
        viewModelScope.launch {
            settingsRepository.getUserId().collectLatest {
                id -> userId = id
            }
        }
        viewModelScope.launch {
            settingsRepository.getUserName().collectLatest {
                name -> userName = name
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            try {
                val result = authRepository.login(email, password)
                if (result.isSuccess) {
                    uiState = AuthUiState.Success("Login correcto")
                } else {
                    uiState = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                uiState = AuthUiState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            try {
                val result = authRepository.register(name, email, password)
                if (result.isSuccess) {
                    uiState = AuthUiState.Success("Registro correcto")
                } else {
                    uiState = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                uiState = AuthUiState.Error("No se pudo conectar al servidor")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            uiState = AuthUiState.Idle
        }
    }

    fun resetState() {
        uiState = AuthUiState.Idle
    }
}
