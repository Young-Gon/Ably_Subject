package com.gondev.ably.subject.di

import com.gondev.ably.subject.repository.FavoritesRepository
import com.gondev.ably.subject.repository.FavoritesRepositoryImpl
import com.gondev.ably.subject.repository.ProductsRepository
import com.gondev.ably.subject.repository.ProductsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {
    @Binds
    @ViewModelScoped
    abstract fun bindProductsRepository(
        ProductsRepository: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @ViewModelScoped
    abstract fun bindFavoritesRepository(
        favoritesRepository: FavoritesRepositoryImpl
    ): FavoritesRepository
}
