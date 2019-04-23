package com.ratanparai.moviedog.db.repository

import com.ratanparai.moviedog.db.dao.MovieDao

class MovieRepository private constructor(private val movieDao : MovieDao){

    companion object {
        @Volatile private var instance : MovieRepository? = null

        fun getInstance(movieDao: MovieDao) = instance ?: synchronized(this) {
            instance ?: MovieRepository(movieDao).also { instance = it }
        }
    }
}