package com.example.practicafinallibros.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.practicafinallibros.data.local.PreferenceKeys
import com.example.practicafinallibros.data.local.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val context: Context) {

    fun observeDarkMode(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_MODE] = enable
        }
    }

    fun observeLanguage(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LANGUAGE] ?: "es"
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTH_TOKEN] = token
        }
    }

    fun getAuthToken(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.AUTH_TOKEN]
    }

    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ROLE] = role
        }
    }

    fun getUserRole(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ROLE]
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = userId
        }
    }

    fun getUserId(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ID]
    }

    suspend fun saveUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_NAME] = userName
        }
    }

    fun getUserName(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_NAME]
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.AUTH_TOKEN)
            preferences.remove(PreferenceKeys.USER_ROLE)
            preferences.remove(PreferenceKeys.USER_ID)
            preferences.remove(PreferenceKeys.USER_NAME)
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.AUTH_TOKEN] != null
    }

    fun isUserAdmin(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ROLE]?.lowercase() == "admin"
    }
}