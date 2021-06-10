package com.gondev.ably.subject.modlule.network.response

import com.gondev.ably.subject.modlule.database.entify.ProductEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ProductList(
    val banners: List<Banner> = emptyList(),
    @SerialName("goods")
    val products: List<Product>,
)

@Serializable
data class Banner(
    val id: Int,
    val image: String,
)

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val image: String,
    val actual_price: Int,
    val price: Int,
    val is_new: Boolean,
    val sell_count: Int,
)