package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.local.entity.UserEntity
import com.example.practicafinallibros.data.repository.UserRepository
import com.example.practicafinallibros.ui.state.AdminUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: UserRepository
): ViewModel() {

    var userList by mutableStateOf<List<UserEntity>>(emptyList())
        private set

    var uiState by mutableStateOf<AdminUiState>(AdminUiState.Idle)
        private set

    init {
        viewModelScope.launch {
            repository.observeCache().collectLatest {
                cachedUsers -> userList = cachedUsers
            }
        }
    }

    fun refreshUsers(token: String) {
        viewModelScope.launch {
            uiState = AdminUiState.Loading
            try {
                repository.refresh(token)
                uiState = AdminUiState.Idle
            } catch (e: Exception) {
                uiState = AdminUiState.Error("No se pudo cargar la información. Revisa tu conexión.")
            }
        }
    }

    fun updateUserName(token: String, userId: String, newName: String) {
        viewModelScope.launch {
            uiState = AdminUiState.Loading
            try {
                repository.updateUserName(token, userId, newName)
                uiState = AdminUiState.Success("Nombre actualizado correctamente")
            } catch (e: Exception) {
                uiState = AdminUiState.Error("Error al actualizar el usuario")
            }
        }
    }

    fun deleteUser(token: String, userId: String) {
        viewModelScope.launch {
            uiState = AdminUiState.Loading
            try {
                repository.deleteUser(token, userId)
                uiState = AdminUiState.Success("Usuario eliminado correctamente")
            } catch (e: Exception) {
                uiState = AdminUiState.Error("Error al eliminar el usuario")
            }
        }
    }

    fun resetState() {
        uiState = AdminUiState.Idle
    }
}
