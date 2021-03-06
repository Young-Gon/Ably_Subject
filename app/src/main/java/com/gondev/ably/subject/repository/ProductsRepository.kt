package com.gondev.ably.subject.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.gondev.ably.subject.model.database.AppDatabase
import com.gondev.ably.subject.model.database.entify.BannerEntity
import com.gondev.ably.subject.model.dto.ProductType
import com.gondev.ably.subject.model.network.ProductsAPI
import timber.log.Timber
import javax.inject.Inject

interface ProductsRepository{
    val pager: Pager<Int, ProductType>
    val banners: LiveData<List<BannerEntity>>
}

class ProductsRepositoryImpl  @Inject constructor(
    database: AppDatabase,
    api: ProductsAPI,
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

@ExperimentalPagingApi
class ProductsListRemoteMediator(
    private val database: AppDatabase,
    private val api: ProductsAPI,
): RemoteMediator<Int, ProductType>() {

    private val productDao = database.getProductDao()
    private val bannerDao = database.getBannerDao()
    private var lastKey: Int? = null

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductType>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                REFRESH -> null
                PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                APPEND ->
                    lastKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }

            val (banners, productList) = loadKey?.let { api.requestGetProductList(it) } ?: api.requestGetFirstProductList()

            database.withTransaction {
                // ?????? ??????
                if (loadType == REFRESH) {
                    // ?????? ?????? ????????? ??????
                    productDao.clearAll()
                    bannerDao.clearAll()
                    
                    bannerDao.insertAll(banners)
                }

                // '?????????' ????????? ?????? ProductResponse POJO???
                // OnConflictStrategy.REPLACE??? ?????? ??? ??????
                // '?????????' ????????? default value??? ???????????? ??????
                // OnConflictStrategy.IGNORE??? ????????????
                // ?????? ????????? ?????? update??????
                // https://tech.bakkenbaeck.com/post/room-insert-update
                // onDelete = ForeignKey.CASCADE
                // ??? ???????????? OnConflictStrategy.REPLACE??? ?????? ??????
                // ???????????? ?????? ?????? ??????
                productDao.insertOrUpdate(productList)
                lastKey = productList.lastOrNull()?.id
            }

            MediatorResult.Success(endOfPaginationReached = productList.size < 10)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

}