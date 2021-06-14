package com.gondev.ably.subject.model.database.entify

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 홈 화면 배너에서 사용하는 데이터 입니다
 * @see [Banner][com.gondev.ably.subject.model.network.response.BannerResponse]
 */
@Entity(tableName = "banner")
data class BannerEntity (
    @PrimaryKey
    val id: Int,
    val image: String,
)
