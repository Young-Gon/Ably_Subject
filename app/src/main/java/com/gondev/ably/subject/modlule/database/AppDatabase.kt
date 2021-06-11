package com.gondev.ably.subject.modlule.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gondev.ably.subject.modlule.database.dao.BannerDao
import com.gondev.ably.subject.modlule.database.dao.ProductDao
import com.gondev.ably.subject.modlule.database.entify.BannerEntity
import com.gondev.ably.subject.modlule.database.entify.ProductEntity

/**
 * 데이터베이스 모듈입니다
 * 케쉬 기능과 favorates 저장 기능을 위해 추가 하였습니다
 * 모든 entity와 dao를 관리하고
 * 마이그레이션을 진행 합니다
 */
@Database(
    entities = [
        ProductEntity::class,
        BannerEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getProductDao(): ProductDao
    abstract fun getBannerDao(): BannerDao
}