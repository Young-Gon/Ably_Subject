package com.gondev.ably.subject.model.dto

/**
 * 홈화면에서 베너와 상품 리스트를 같이 보여 주기 위해
 * 멀티 타입으로 구성하기 위해 사용 합니다
 * ```
 * val homeRecyclerviewList: List<ListType> = listOf(
 *     BannerType(),
 *     ProductType(),
 *     ProductType(),
 *     ...
 * )
 * ```
 */
sealed class ListType {
    abstract val id: Int
}

/**
 * 상품 정보타입 및
 * [ProductEntity][com.gondev.ably.subject.model.database.entify.ProductEntity]의 POJO 입니다
 */
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