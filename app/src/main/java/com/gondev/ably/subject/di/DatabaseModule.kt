package com.gondev.ably.subject.di

import android.app.Application
import androidx.room.Room
import com.gondev.ably.subject.model.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI 라이브러리에서 사용하는 데이터베이스 모듈입니다
 * ViewModel에 DAO 등을 받을 때 사용 합니다
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(application: Application) =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "Image_Database"
        ).build()

    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase) =
        appDatabase.getProductDao()
}
