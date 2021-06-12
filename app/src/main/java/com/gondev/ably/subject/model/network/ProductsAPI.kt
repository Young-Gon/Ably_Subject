package com.gondev.ably.subject.model.network

import com.gondev.ably.subject.model.network.response.ProductList
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsAPI {
    @GET("api/home")
    suspend fun fetchGetFirstProductList(): ProductList

    @GET("api/home/goods")
    suspend fun fetchGetProductList(@Query("lastId") lastId: Int): ProductList
}