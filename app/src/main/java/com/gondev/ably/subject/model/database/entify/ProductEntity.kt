package com.gondev.ably.subject.model.database.entify

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 상품 정보를 나타내는 데이터 테이블 엔티티 입니다
 * **좋아요** 필드가 추가 되어 있습니다
 * @see [Product][com.gondev.ably.subject.model.network.response.ProductResponse]
 * @see [ProductType][com.gondev.ably.subject.model.dto.ProductType]
 */
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