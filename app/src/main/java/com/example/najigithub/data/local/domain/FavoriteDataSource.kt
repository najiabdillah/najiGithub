package com.example.najigithub.data.local.domain

import favoritedatabase.favoritedb.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteDataSource {

    suspend fun getFavoriteById(id: Long): FavoriteEntity?

    fun getAllFavorite(): Flow<List<FavoriteEntity>>

    suspend fun deleteFavoriteById(id: Long)

    suspend fun insertFavorite(name: String, image: String, id: Long? = null)
}