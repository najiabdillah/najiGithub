package com.example.najigithub.presentation.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.najigithub.data.remote.dto.SearchResponse
import com.example.najigithub.domain.repository.GithubRepository
import com.example.najigithub.domain.utils.Extensions
import com.example.najigithub.domain.utils.Result
import com.example.najigithub.domain.utils.UiEvent
import com.example.najigithub.domain.utils.UiText
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

class SearchViewModel(
    private val repository: GithubRepository
): ViewModel() {

    private var _loadingState = MutableStateFlow(false)
    val loadingState get() = _loadingState.asSharedFlow()

    private var _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    private var searchJob: Job? = null

    fun getSearch(username: String, onSuccess: (SearchResponse) -> Unit) {

           searchJob =  repository.searchUser(username).onEach { result ->
                _loadingState.value = true
                when(result) {
                    is Result.Loading -> {
                        _loadingState.value = true
                    }
                    is Result.Success -> {
                        _loadingState.value = false
                        result.data?.let(onSuccess)
                    }
                    is Result.Error -> {
                        _loadingState.value = false
                        result.message?.let { msg ->
                            _uiEvent.emit(
                                UiEvent.ShowSnackbar(
                                    UiText.DynamicString(
                                        msg,
                                        Extensions.MESSAGE_ERROR
                                    ),
                                    Extensions.MESSAGE_ERROR
                                )
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope + Main)
    }

    suspend fun cancelRunningJob() {
        searchJob?.cancelAndJoin()
    }
}