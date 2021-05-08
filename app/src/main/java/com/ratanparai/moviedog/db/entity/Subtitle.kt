package com.ratanparai.moviedog.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "subtitle", indices = [Index(value = ["subtitleUrl"], unique = true)])
data class Subtitle(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    val movieId: Int,
    val subtitleUrl: String
)
