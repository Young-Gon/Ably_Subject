package com.gondev.ably.subject.model.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.gondev.ably.subject.model.database.entify.ProductEntity
import com.gondev.ably.subject.model.dto.ProductType
import com.gondev.ably.subject.model.network.response.ProductResponse

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun pagingSource(): PagingSource<Int, ProductType>

    @Query("SELECT * FROM product WHERE favorite == 1")
    fun findAllByFavorites(): LiveData<List<ProductType>>

    @Insert(entity = ProductEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<ProductResponse>): List<Long>

    @Update(entity = ProductEntity::class)
    suspend fun updateAll(products: List<ProductResponse>)

    @Update(entity = ProductEntity::class)
    suspend fun update(products: ProductType)

    @Query("DELETE FROM product WHERE favorite == 0")
    suspend fun clearAll()

    @Transaction
    suspend fun insertOrUpdate(products: List<ProductResponse>) {
        val updateList = mutableListOf<ProductResponse>()
        insertAll(products).forEachIndexed { index, insertedId ->
            if(insertedId == -1L)
                updateList.add(products[index])
        }

        if (updateList.isNotEmpty()) updateAll(updateList)
    }
}