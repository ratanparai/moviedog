package com.ratanparai.moviedog.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovieWithMovieUrls (
    @Embedded val movie: Movie,
    @Relation(parentColumn = "id", entityColumn = "movieId", entity = MovieUrl::class) val movieUrls : List<MovieUrl>
)