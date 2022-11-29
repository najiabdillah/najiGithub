package com.example.najigithub.domain.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.najigithub.domain.utils.Extensions.DATA_FIRST_INSTALL
import com.example.najigithub.domain.utils.Extensions.DATA_PROFILE
import com.example.najigithub.domain.utils.Extensions.DATA_SETTING_MODE
import com.example.najigithub.domain.utils.Extensions.FIRST_INSTALL
import com.example.najigithub.domain.utils.Extensions.MODE_VIEW
import com.example.najigithub.domain.utils.Extensions.USERNAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionManager(private val context: Context) {

    suspend fun setUsername(username: String) {
        context.dataStoreUser.edit { result ->
            result[DATA_USERNAME] = username
        }
    }

    val getUsername: Flow<String> get() = context.dataStoreUser.data.map { preference ->
            preference[DATA_USERNAME] ?: " "
        }

    suspend fun setMode(state: Boolean) {
        context.dataStoreMode.edit { result ->
            result[THEME] = state
        }
    }

    val getMode: Flow<Boolean> get() = context.dataStoreMode.data.map { preference ->
        preference[THEME] ?: false
    }

    suspend fun setFirstInstall(state: Boolean) {
        context.dataStoreWelcome.edit { result ->
            result[INSTALL] = state
        }
    }

    val getFirstIntall: Flow<Boolean> get() = context.dataStoreWelcome.data.map { preferences ->
        preferences[INSTALL] ?: false
    }

    companion object {
        private val Context.dataStoreUser: DataStore<Preferences> by preferencesDataStore(
            name = DATA_PROFILE
        )
        private val Context.dataStoreMode: DataStore<Preferences> by preferencesDataStore(
            name = DATA_SETTING_MODE
        )
        private val Context.dataStoreWelcome: DataStore<Preferences> by preferencesDataStore(
            name = DATA_FIRST_INSTALL
        )
        val DATA_USERNAME = stringPreferencesKey(
            name = USERNAME
        )
        val THEME = booleanPreferencesKey(
            name = MODE_VIEW
        )
        val INSTALL = booleanPreferencesKey(
            name = FIRST_INSTALL
        )
    }
}