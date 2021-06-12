package com.gondev.ably.subject.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.gondev.ably.subject.model.database.AppDatabase
import com.gondev.ably.subject.model.database.entify.BannerEntity
import com.gondev.ably.subject.model.database.entify.ProductEntity
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
                    lastKey ?: return MediatorResult.Success(endOfPaginationReached = false)
            }

            val (banners, productList) = loadKey?.let { api.fetchGetProductList(it) }?: api.fetchGetFirstProductList()

            database.withTransaction {
                // 베너 추가
                if (loadType == REFRESH) {
                    // 디비 에서 데이터 삭제
                    productDao.clearAll()
                    bannerDao.clearAll()
                    
                    bannerDao.insertAll(banners)
                }

                // 좋아요 표시한 정보가 사라지지 않도록
                // 디비값으로 갱신 후 삽입
                val favorites = productDao.findAllByFavorites()
                val productEntityList= productList.map {  product ->
                    ProductEntity(
                        id = product.id,
                        name = product.name,
                        image = product.image,
                        actual_price = product.actual_price,
                        price = product.price,
                        is_new = product.is_new,
                        sell_count = product.sell_count,
                        favorite = favorites.any { it.id == product.id }
                    )
                }

                productDao.insertAll(productEntityList)
                lastKey = productList.lastOrNull()?.id
            }

            MediatorResult.Success(endOfPaginationReached = productList.size < 10)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

}