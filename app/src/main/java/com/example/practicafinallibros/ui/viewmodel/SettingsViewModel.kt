package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.repository.SettingsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    var darkMode by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            settingsRepository.observeDarkMode().collectLatest {
                enable -> darkMode = enable
            }
        }
    }

    fun setterDarkMode(enable: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enable)
        }
    }
}
