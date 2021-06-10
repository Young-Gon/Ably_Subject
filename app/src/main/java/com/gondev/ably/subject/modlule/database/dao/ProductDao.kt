package com.gondev.ably.subject.modlule.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.modlule.network.response.Product

@Dao
interface ProductDao {

    @Insert(entity = ProductEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<Product>)

    @Query("SELECT * FROM product")
    fun pagingSource(): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM product WHERE favorite == 0")
    suspend fun clearAll()
}