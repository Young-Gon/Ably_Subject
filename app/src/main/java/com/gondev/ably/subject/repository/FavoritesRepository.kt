package com.gondev.ably.subject.repository

import androidx.lifecycle.LiveData
import com.gondev.ably.subject.model.database.dao.ProductDao
import com.gondev.ably.subject.model.dto.ProductType
import javax.inject.Inject

interface FavoritesRepository {
    val favoritesList: LiveData<List<ProductType>>

    suspend fun updateProduct(product: ProductType)
}

class FavoritesRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
):FavoritesRepository{
    override val favoritesList = productDao.fetchAllByFavorites()

    override suspend fun updateProduct(product: ProductType) {
        productDao.update(product)
    }
}