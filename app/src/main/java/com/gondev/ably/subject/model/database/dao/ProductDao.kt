package com.gondev.ably.subject.model.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.gondev.ably.subject.model.database.entify.ProductEntity
import com.gondev.ably.subject.model.dto.ProductType

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun pagingSource(): PagingSource<Int, ProductType>

    @Query("SELECT * FROM product WHERE favorite == 1")
    fun fetchAllByFavorites(): LiveData<List<ProductType>>

    @Query("SELECT * FROM product WHERE favorite == 1")
    suspend fun findAllByFavorites(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM product WHERE favorite == 0")
    suspend fun clearAll()

    @Update(entity = ProductEntity::class)
    suspend fun update(product: ProductType)
}