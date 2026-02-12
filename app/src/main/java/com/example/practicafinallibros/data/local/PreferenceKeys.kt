package com.example.practicafinallibros.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
    val USER_ROLE = stringPreferencesKey("user_role")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val LANGUAGE = stringPreferencesKey("language")
}