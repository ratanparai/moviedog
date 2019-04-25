package com.ratanparai.moviedog.db.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ratanparai.moviedog.db.entity.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY title")
    fun getMovies() : List<Movie>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovieById(movieId : Int) : Movie

    @Query("SELECT * FROM movies WHERE imdbId = :imdbId")
    fun getMovieByImdbId(imdbId : String) : Movie

    @Insert
    fun insertMovie(movie: Movie): Long

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :title || '%'")
    fun searchByTitle(title: String): List<Movie>
}