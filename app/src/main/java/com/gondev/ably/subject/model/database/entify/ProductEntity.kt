package com.gondev.ably.subject.model.database.entify

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val image: String,
    val actual_price: Int,
    val price: Int,
    val is_new: Boolean,
    val sell_count: Int,
    @ColumnInfo(defaultValue = "0")
    val favorite: Boolean,
)