package com.ratanparai.moviedog.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ratanparai.moviedog.db.entity.SearchHash

@Dao
interface SearchHashDao {
    @Insert
    fun add(searchHash: SearchHash): Long

    @Query("SELECT * FROM searchHash WHERE url = :searchUrl")
    fun getByUrl(searchUrl: String): SearchHash?

    @Query("UPDATE searchHash SET md5hash = :hash WHERE id = :id")
    fun updateHash(id: Int, hash: String)
}