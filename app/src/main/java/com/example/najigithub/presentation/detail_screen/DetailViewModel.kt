package com.example.najigithub.presentation.detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.najigithub.data.remote.dto.FollowResponse
import com.example.najigithub.data.remote.dto.UserDetailResponse
import com.example.najigithub.domain.repository.GithubRepository
import com.example.najigithub.domain.utils.Extensions.MESSAGE_ERROR
import com.example.najigithub.domain.utils.Result
import com.example.najigithub.domain.utils.UiEvent
import com.example.najigithub.domain.utils.UiText
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

class DetailViewModel(
    private val repository: GithubRepository
): ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    private val state get() = _state.asStateFlow()

    private var _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent get() = _uiEvent.asSharedFlow()

    private var _loadingState = MutableStateFlow(false)
    val loadingState get() = _loadingState.asStateFlow()

    fun getDetailUser(username: String, onSuccess: (UserDetailResponse) -> Unit) {
        _state.value = state.value.copy(
            username = username
        )
        repository.userDetail(username).onEach { result ->
            when(result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    result.data?.let(onSuccess)
                }
                is Result.Error -> {
                    result.message?.let { msg ->
                        _uiEvent.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(
                                    msg,
                                    MESSAGE_ERROR
                                ),
                                MESSAGE_ERROR
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope + Main)
    }

    fun getUserFollowing(onSuccess: (List<FollowResponse>) -> Unit) {
        repository.userFollowing(state.value.username.toString()).onEach { result ->
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
                                     MESSAGE_ERROR
                                 ),
                                 MESSAGE_ERROR
                             )
                         )
                    }
                }
            }
        }.launchIn(viewModelScope + Main)
    }

    fun getUserFollower(onSuccess: (List<FollowResponse>) -> Unit) {
        repository.userFollower(state.value.username.toString()).onEach { result ->
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
                                    MESSAGE_ERROR
                                ),
                                MESSAGE_ERROR
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope + Main)
    }
}