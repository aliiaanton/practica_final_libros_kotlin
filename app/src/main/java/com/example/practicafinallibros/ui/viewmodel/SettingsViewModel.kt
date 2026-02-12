package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    var darkMode by mutableStateOf(false)
        private set

    val darkModeFlow = settingsRepository.observeDarkMode()
    
    val language = settingsRepository.observeLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "es")

    init {
        viewModelScope.launch {
            settingsRepository.observeDarkMode().collect { enable ->
                darkMode = enable
            }
        }
    }

    fun updateDarkMode(enable: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enable)
        }
    }

    fun setLanguage(language: String): Job {
        return viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }
}
