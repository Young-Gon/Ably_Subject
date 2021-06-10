package com.gondev.ably.subject.repository

import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.gondev.ably.subject.modlule.database.AppDatabase
import com.gondev.ably.subject.modlule.database.dao.ProductDao
import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import com.gondev.ably.subject.modlule.network.ProductsAPI
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

interface ProductsRepository{
    val pager: Pager<Int, ProductEntity>
}

class ProductsRepositoryImpl  @Inject constructor(
    val database: AppDatabase,
    val api: ProductsAPI,
):ProductsRepository {
    private val productDao: ProductDao = database.getProductDao()

    @OptIn(ExperimentalPagingApi::class)
    override val pager = Pager(
        config = PagingConfig(pageSize = 50),
        remoteMediator = ProductsListRemoteMediator(database, api)
    ) {
        productDao.pagingSource()
    }
}

@OptIn(ExperimentalPagingApi::class)
class ProductsListRemoteMediator(
    private val database: AppDatabase,
    private val api: ProductsAPI,
): RemoteMediator<Int, ProductEntity>() {
    private val productDao: ProductDao = database.getProductDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    lastItem.id
                }
            }

            val (banners, productList) = loadKey?.let { api.fetchGetProductList(it) }?: api.fetchGetFirstProductList()

            database.withTransaction {
                // TODO 베너 추가
                if (loadType == LoadType.REFRESH) {
                    productDao.clearAll()
                }

                productDao.insertAll(productList)
            }

            MediatorResult.Success(endOfPaginationReached = productList.size < 10)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

}