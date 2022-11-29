package com.example.najigithub.data.local.data


import com.example.najigithub.FavoriteDatabase
import com.example.najigithub.data.local.domain.FavoriteDataSource
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import favoritedatabase.favoritedb.FavoriteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteDataSourceImpl(
    db: FavoriteDatabase
): FavoriteDataSource {

    private val queries = db.favoriteEntityQueries

    override suspend fun getFavoriteById(id: Long): FavoriteEntity? {
        return withContext(Dispatchers.IO) {
            queries.getFavoritenById(id).executeAsOneOrNull()
        }
    }

    override fun getAllFavorite(): Flow<List<FavoriteEntity>> {
        return queries.getAllFavorite().asFlow().mapToList()
    }

    override suspend fun deleteFavoriteById(id: Long) {
        return withContext(Dispatchers.IO) {
            queries.deleteFavoriteById(id)
        }
    }

    override suspend fun insertFavorite(name: String, image: String, id: Long?) {
        return withContext(Dispatchers.IO) {
            queries.insertFavorite(id, name, image)
        }
    }

}