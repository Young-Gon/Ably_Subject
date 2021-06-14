package com.gondev.ably.subject.model.network

import com.gondev.ably.subject.model.network.response.ProductListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsAPI {
    @GET("api/home")
    suspend fun requestGetFirstProductList(): ProductListResponse

    @GET("api/home/goods")
    suspend fun requestGetProductList(@Query("lastId") lastId: Int): ProductListResponse
}