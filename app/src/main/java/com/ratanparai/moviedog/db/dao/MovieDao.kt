package com.ratanparai.moviedog.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ratanparai.moviedog.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY title")
    fun getMovies() : LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovie(movieId : Int) : LiveData<Movie>

    @Query("SELECT * FROM movies WHERE imdbId = :imdbId")
    fun getMovie(imdbId : String) : LiveData<Movie>
}