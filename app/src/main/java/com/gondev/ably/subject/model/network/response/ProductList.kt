package com.gondev.ably.subject.model.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductListResponse(
    val banners: List<BannerResponse> = emptyList(),
    @SerialName("goods")
    val products: List<ProductResponse>,
)

@Serializable
data class BannerResponse(
    val id: Int,
    val image: String,
)

/**
 * 네트워크 결과값입니다
 * **좋아요** 항목이 빠저 있습니다
 */
@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val image: String,
    val actual_price: Int,
    val price: Int,
    val is_new: Boolean,
    val sell_count: Int,
)