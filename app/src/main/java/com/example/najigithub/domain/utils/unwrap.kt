package com.example.najigithub.domain.utils

import com.example.najigithub.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

inline fun <reified T> unwrapAsFlow(crossinline apiCall: suspend () -> T): Flow<Result<T>> =
    channelFlow {
        send(Result.Loading())
        try {
            val data = apiCall()
            send(Result.Success(data))
        } catch (e: HttpException) {
            e.printStackTrace()
            send(
                Result.Error(
                data = e.response()?.errorBody()?.charStream()?.fromJson(),
                uiText = when {
                    e.code() == 401 -> {
                        UiText.StringResource(R.string.unwrap_cannot_retrieve_data_text)
                    }
                    e.code() == 503 -> {
                        UiText.StringResource(R.string.server_maintenace)
                    }
                    (500..550).contains(e.code()) -> {
                        UiText.StringResource(R.string.unwrap_timeout_error_text)
                    }
                    else -> {
                        UiText.StringResource(R.string.unwrap_cannot_retrieve_data_text)
                    }
                }
            ))
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            send(Result.Error(uiText = UiText.StringResource(R.string.unwrap_timeout_error_text)))
        } catch (e: IOException) {
            e.printStackTrace()
            send(Result.Error(uiText = UiText.StringResource(R.string.unwrap_network_error_text)))
        } catch (e: Exception) {
            e.printStackTrace()
            send(Result.Error(uiText = UiText.unknownError()))
        }
    }