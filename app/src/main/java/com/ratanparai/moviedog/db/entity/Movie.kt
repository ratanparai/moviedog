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
    val backgroundImage : String?,
    val videoUrl : String,
    val contentType : String?,
    val live : Boolean?,
    val width : Int,
    val height : Int,
    val audioChannelConfig : String?,
    val purchasePrice : String?,
    val rentalPrice : String?,
    val ratingStyle: Int?,
    val ratingScore: Double,
    val productionYear : Int,
    val duration: Int
)
