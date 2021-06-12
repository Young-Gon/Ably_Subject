package com.gondev.ably.subject.model.dto

sealed class ListType {
    abstract val id: Int
}

data class ProductType(
    override val id: Int,
    val name: String,
    val image: String,
    val actual_price: Int,
    val price: Int,
    val is_new: Boolean,
    val sell_count: Int,
    val favorite: Boolean,
): ListType()

data class BannerType(
    override val id: Int=-1
): ListType()