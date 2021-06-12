package com.gondev.ably.subject.model.database.entify

import androidx.room.Entity
import androidx.room.PrimaryKey

interface IProductEntity {
    val id: Int
    val name: String
    val image: String
    val actual_price: Int
    val price: Int
    val is_new: Boolean
    val sell_count: Int
    val favorite: Boolean
}

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    override val id: Int,
    override val name: String,
    override val image: String,
    override val actual_price: Int,
    override val price: Int,
    override val is_new: Boolean,
    override val sell_count: Int,
    override val favorite: Boolean,
) : IProductEntity