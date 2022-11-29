package com.example.najigithub.domain.utils

sealed class Result<T>(
    val data: T? = null,
    val message: String? = null,
    val uiText: UiText? = null
) {
    class Success<T> (data: T): Result<T>(data)
    class Error<T> (data: T? = null, message: String? = null, uiText: UiText? = null): Result<T>(data, message, uiText)
    class Loading<T> (data: T? = null): Result<T>()
}
