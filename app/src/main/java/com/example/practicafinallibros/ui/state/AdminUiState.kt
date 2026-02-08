package com.example.practicafinallibros.ui.state

sealed class AdminUiState {

    data object Idle: AdminUiState()
    data object Loading: AdminUiState()
    data class Success(val message: String): AdminUiState()
    data class Error(val message: String): AdminUiState()

}
