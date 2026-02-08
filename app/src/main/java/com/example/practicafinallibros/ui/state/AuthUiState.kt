package com.example.practicafinallibros.ui.state

sealed class AuthUiState {

    data object Idle: AuthUiState()
    data object Loading: AuthUiState()
    data class Success(val message: String): AuthUiState()
    data class Error(val message: String): AuthUiState()

}