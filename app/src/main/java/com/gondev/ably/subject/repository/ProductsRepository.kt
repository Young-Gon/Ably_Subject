package com.gondev.ably.subject.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.gondev.ably.subject.modlule.database.AppDatabase
import com.gondev.ably.subject.modlule.database.entify.BannerEntity
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.modlule.network.ProductsAPI
import timber.log.Timber
import javax.inject.Inject

interface ProductsRepository{
    val pager: Pager<Int, ProductEntity>
    val banners: LiveData<List<BannerEntity>>
}

class ProductsRepositoryImpl  @Inject constructor(
    val database: AppDatabase,
    val api: ProductsAPI,
):ProductsRepository {
    private val productDao = database.getProductDao()
    private val bannerDao = database.getBannerDao()

    @OptIn(ExperimentalPagingApi::class)
    override val pager = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = ProductsListRemoteMediator(database, api)
    ) {
        productDao.pagingSource()
    }

    override val banners = bannerDao.findAll()
}

@OptIn(ExperimentalPagingApi::class)
class ProductsListRemoteMediator(
    private val database: AppDatabase,
    private val api: ProductsAPI,
): RemoteMediator<Int, ProductEntity>() {
    private val productDao = database.getProductDao()
    private val bannerDao = database.getBannerDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                REFRESH -> null.also { Timber.v("REFRESH") }
                PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true).also { Timber.v("PREPEND") }
                APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = false)

                    lastItem.id
                }
            }
            Timber.v("loadkey=$loadKey")

            val (banners, productList) = loadKey?.let { api.fetchGetProductList(it) }?: api.fetchGetFirstProductList()

            database.withTransaction {
                // 베너 추가
                if (loadType == LoadType.REFRESH) {
                    Timber.e("clear db")
                    productDao.clearAll()
                    bannerDao.clearAll()
                    
                    bannerDao.insertAll(banners)
                }

                productDao.insertAll(productList)
            }

            Timber.v("productList.size=${productList.size}, endOfPaginationReached = ${productList.size < 10}")
            MediatorResult.Success(endOfPaginationReached = productList.size < 10)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

}