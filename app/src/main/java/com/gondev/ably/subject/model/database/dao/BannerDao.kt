package com.gondev.ably.subject.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gondev.ably.subject.model.database.entify.BannerEntity
import com.gondev.ably.subject.model.network.response.Banner

@Dao
interface BannerDao {

    @Insert(entity = BannerEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banners: List<Banner>)

    @Query("SELECT * FROM banner")
    fun findAll(): LiveData<List<BannerEntity>>

    @Query("DELETE FROM banner")
    suspend fun clearAll()
}