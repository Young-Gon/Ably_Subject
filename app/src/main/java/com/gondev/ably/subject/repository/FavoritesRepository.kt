package com.gondev.ably.subject.repository

import androidx.lifecycle.LiveData
import com.gondev.ably.subject.modlule.database.dao.ProductDao
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import javax.inject.Inject

interface FavoritesRepository {
    val favoritesList: LiveData<List<ProductEntity>>

    suspend fun updateProduct(product: ProductEntity)
}

class FavoritesRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
):FavoritesRepository{
    override val favoritesList = productDao.FetchfindAllByFavorites()

    override suspend fun updateProduct(product: ProductEntity) {
        productDao.update(product)
    }
}