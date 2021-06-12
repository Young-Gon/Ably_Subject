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

            val (banners, productList) = loadKey?.let { api.fetchGetProductList(it) }?: api.fetchGetFirstProductList()

            database.withTransaction {
                // 베너 추가
                if (loadType == REFRESH) {
                    // 디비 에서 데이터 삭제
                    productDao.clearAll()
                    bannerDao.clearAll()
                    
                    bannerDao.insertAll(banners)
                }

                // '좋아요' 필드가 없는 Product POJO를
                // OnConflictStrategy.REPLACE로 삽입 할 경우
                // '좋아요' 표시가 default value로 업데이트 된다
                // OnConflictStrategy.IGNORE로 삽입하고
                // 삽입 실패한 행만 update하자
                // https://tech.bakkenbaeck.com/post/room-insert-update
                // onDelete = ForeignKey.CASCADE
                // 인 경우에도 OnConflictStrategy.REPLACE를 사용 하면
                // 안된다고 하니 주의 하자
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