package com.ratanparai.moviedog.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movieUrl")
data class MovieUrl(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    val movieId: Int,
    val movieUrl: String,
    val serviceName: String
)