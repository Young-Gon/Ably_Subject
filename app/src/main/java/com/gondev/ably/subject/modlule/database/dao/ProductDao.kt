package com.gondev.ably.subject.modlule.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.gondev.ably.subject.modlule.database.entify.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun pagingSource(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM product WHERE favorite == 1")
    fun FetchfindAllByFavorites(): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM product WHERE favorite == 1")
    suspend fun findAllByFavorites(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM product WHERE favorite == 0")
    suspend fun clearAll()

    @Update
    suspend fun update(product: ProductEntity)
}