package com.gondev.ably.subject.model.database.entify

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banner")
data class BannerEntity (
    @PrimaryKey
    val id: Int,
    val image: String,
)