package com.ratanparai.moviedog.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ratanparai.moviedog.db.entity.MovieUrl

@Dao
interface MovieUrlDao {

    @Insert
    fun insertMovieUrl(movieUrl: MovieUrl)

    @Query("SELECT * FROM movieUrl WHERE movieId = :id")
    fun getMovieUrlsByMovieId(id: Int): List<MovieUrl>
}