package com.example.najigithub.presentation.favorite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.najigithub.data.local.domain.FavoriteDataSource
import com.example.najigithub.domain.utils.Extensions.MESSAGE_ERROR
import com.example.najigithub.domain.utils.Extensions.MESSAGE_SUCCESS
import com.example.najigithub.domain.utils.UiEvent
import com.example.najigithub.domain.utils.UiText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val database: FavoriteDataSource
) : ViewModel() {

    private var _getAllFavorite = database.getAllFavorite()

    val getAllFavorite get() = _getAllFavorite

    private var _uiEvent = MutableSharedFlow<UiEvent>()

    val uiEvent get() = _uiEvent.asSharedFlow()

    private var _stateVisibility = MutableStateFlow(false)

    val stateVisibility get() = _stateVisibility.asStateFlow()

    fun setInsertFavorite(name: String, image: String) = viewModelScope.launch(IO) {

        if (name.isEmpty() && image.isEmpty()) {
            _uiEvent.emit(
                UiEvent.ShowSnackbar(
                    UiText.DynamicString(
                        "Please filled the empty field",
                        MESSAGE_ERROR
                    ),
                    MESSAGE_ERROR
                )
            )
        } else {
            _uiEvent.emit(
                UiEvent.ShowSnackbarWithCallback(
                    UiText.DynamicString(
                        "Berhasil menjadi favorite, Harap menunggu.",
                        MESSAGE_SUCCESS
                    ),
                    MESSAGE_SUCCESS
                )
            )
            database.insertFavorite(name, image)
        }
    }

    fun deleteFavorite(id: Long) = viewModelScope.launch(IO) {
        if (id <  1) {
            _uiEvent.emit(
                UiEvent.ShowSnackbar(
                    UiText.DynamicString(
                        "Tidak ada Favorite yang tersedia $id",
                        MESSAGE_ERROR
                    ),
                    MESSAGE_ERROR
                )
            )
        } else {
            database.deleteFavoriteById(id)
        }
    }

    fun checkFavorite(name: String) = viewModelScope.launch(IO) {
        getAllFavorite.collectLatest { stateItem ->
            stateItem.map { dataItem ->
                _stateVisibility.value = dataItem.NAME.contains(name)
            }
        }
    }
}