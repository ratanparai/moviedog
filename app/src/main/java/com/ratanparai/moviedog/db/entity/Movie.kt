package com.ratanparai.moviedog.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie (
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    val title : String,
    val description : String,
    val imdbId : String,
    val cardImage : String,
    val videoUrl : String,
    val productionYear : Int,
    val duration: Int
)
