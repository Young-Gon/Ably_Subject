package com.gondev.ably.subject.modlule.database.entify

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
"id": Int, // 상품 ID
"name": String, // 상품 이름
"image": String, // 상품 이미지 url
"actual_price": Int, // 상품 기본 가격
"price": Int, // 상품 실제 가격(기본가격 X 할인율 / 100 = 실제가격),
"is_new": Boolean, // 신상품인지 여부
"sell_count": Int, // 구매중 갯수
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
    @ColumnInfo(defaultValue = "false")
    val favorite: Boolean,
)
