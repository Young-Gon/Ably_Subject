package com.gondev.ably.subject.model.dto

import com.gondev.ably.subject.model.database.entify.IProductEntity


sealed class ListType {
    abstract val id: Int
}

data class ProductType(
    override val id: Int,
    override val name: String,
    override val image: String,
    override val actual_price: Int,
    override val price: Int,
    override val is_new: Boolean,
    override val sell_count: Int,
    override val favorite: Boolean,
): ListType(), IProductEntity

data class BannerType(
    override val id: Int=-1
): ListType()