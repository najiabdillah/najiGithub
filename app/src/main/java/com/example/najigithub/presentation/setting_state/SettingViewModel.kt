package com.example.najigithub.presentation.setting_state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.najigithub.domain.utils.SessionManager
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingViewModel(
    private val sessionManager: SessionManager
): ViewModel() {

    private var _state = MutableStateFlow(false)

    val state get() = _state.asStateFlow()

    fun getModeSetting() = viewModelScope.launch(IO) {
        sessionManager.getMode.collectLatest { state ->
            _state.value = state
        }
    }

    fun saveModeSetting(state: Boolean) = viewModelScope.launch(IO) {
        sessionManager.setMode(state)
    }
}