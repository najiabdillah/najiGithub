package com.example.najigithub.domain.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.najigithub.R
import com.example.najigithub.domain.utils.Extensions.MESSAGE_ERROR
import com.example.najigithub.domain.utils.Extensions.MESSAGE_SUCCESS
import com.example.najigithub.presentation.favorite_screen.FavoriteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import java.io.Reader

object Extensions {
    const val SEARCH_EP = "/search/users"
    const val DETAIL_EP = "/users/{username}"
    const val FOLLOWER_EP = "/users/{username}/followers"
    const val FOLLOWING_EP = "/users/{username}/following"
    const val AUTH_HEADER = "Authorization"
    const val USERNAME = "Username"
    const val CATEGORY = "Category"
    const val FAVORITE_DATA_USERNAME = "favorite_data_username"
    const val FAVORITE_DATA_IMAGE = "favorite_data_image"
    const val ON_QUERY_TEXT_LISTENER = "onQueryTextListener"
    const val DATA_PROFILE = "data_profile"
    const val DATA_SETTING_MODE = "data_view_mode"
    const val DATA_FIRST_INSTALL = "data_first_install"
    const val MODE_VIEW = "mode_view"
    const val FIRST_INSTALL = "first_install"
    const val MESSAGE_SUCCESS = "message_success"
    const val MESSAGE_ERROR = "message_error"
    const val SEARCH_CATEGORY = "search_category"
    const val FAVORITE_CATEGORY = "favorite_category"
}

inline fun<reified T> Reader.fromJson(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: JsonSyntaxException){
        null
    } catch (e: JsonIOException) {
        null
    }
}

inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, T::class.java)
}

fun Any.toJson(): String {
    return Gson().toJson(this)
}

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.removeView() {
    this.visibility = View.GONE
}

fun ImageView.loadImage(url: String?, cacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE, error: Int = R.drawable.ic_launcher_background) {
    Glide.with(this)
        .load(url)
        .override(480, 320)
        .thumbnail(0.1f)
        .transition(DrawableTransitionOptions.withCrossFade())
        .timeout(20000)
        .error(error)
        .diskCacheStrategy(cacheStrategy)
        .into(this)
}

fun snackbar(view: View, type: String?, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    if (type == MESSAGE_SUCCESS) {
        val snack = Snackbar.make(view, message, duration)
        snack.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.green))
        snack.show()
    } else if (type == MESSAGE_ERROR) {
        val snack = Snackbar.make(view, message, duration)
        snack.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.red))
        snack.show()
    }
}

fun Activity.snackbarWithCallback(view: View, type: String?, message: String, duration: Int = Snackbar.LENGTH_LONG) {
    if (type == MESSAGE_SUCCESS) {
        val snack = Snackbar.make(view, message, duration).addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                startActivity(Intent(this@snackbarWithCallback, FavoriteActivity::class.java))
                finish()
            }
        })
        snack.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.green))
        snack.show()
    }
}