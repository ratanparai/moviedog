package com.ratanparai.moviedog.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "movieUrl", indices = [Index(value = ["movieUrl"], unique = true)])
data class MovieUrl(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    val movieId: Int,
    val movieUrl: String,
    val serviceName: String
)