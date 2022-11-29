package com.example.najigithub.domain.utils

import android.content.Context
import com.example.najigithub.FavoriteDatabase
import com.example.najigithub.data.local.domain.FavoriteDataSource
import com.example.najigithub.data.local.data.FavoriteDataSourceImpl
import com.example.najigithub.data.remote.api.GithubApi
import com.example.najigithub.data.repository.GithubRepositoryImpl
import com.example.najigithub.domain.adapter.FavoriteAdapter
import com.example.najigithub.domain.adapter.FollowAdapter
import com.example.najigithub.domain.adapter.SearchAdapter
import com.example.najigithub.domain.adapter.TabFollowAdapter
import com.example.najigithub.domain.adapter.WelcomeAdapter
import com.example.najigithub.domain.repository.GithubRepository
import com.example.najigithub.presentation.detail_screen.DetailViewModel
import com.example.najigithub.presentation.favorite_screen.FavoriteViewModel
import com.example.najigithub.presentation.search_screen.SearchViewModel
import com.example.najigithub.presentation.setting_state.SettingViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {

    viewModel { SearchViewModel(repository = get()) }

    factory { SearchAdapter() }

    single<GithubRepository> { GithubRepositoryImpl(service = get()) }

    single { ServiceGenerator.createService(serviceClass = GithubApi::class.java) }

    factory { SessionManager(context = get()) }
}

val userDetailModule = module {

    viewModel { DetailViewModel(repository = get()) }

    factory { TabFollowAdapter(fragmentList =  get(), fragmentManager = get(), lifecycle = get() ) }

    factory { FollowAdapter() }

}

val favoriteModule = module {
    viewModel { FavoriteViewModel(database = get()) }

    single<FavoriteDataSource> { FavoriteDataSourceImpl(db = get()) }

    single { provideDriver(context = get()) }

    single { provideDatabase(androidSqliteDriver = get()) }

    factory { FavoriteAdapter() }
}

val settingModule = module {
    viewModel { SettingViewModel(sessionManager = get()) }
}

val welcomeModule = module { 
    factory { WelcomeAdapter() }
}

fun provideDriver(context: Context) = AndroidSqliteDriver(
    schema = FavoriteDatabase.Schema,
    context = context,
    name = "favorite.db"
)

fun provideDatabase(androidSqliteDriver: AndroidSqliteDriver) =
    FavoriteDatabase(
        driver = androidSqliteDriver,
    )
