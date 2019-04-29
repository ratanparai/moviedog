package com.ratanparai.moviedog.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ratanparai.moviedog.db.entity.Scrapped

@Dao
interface ScrappedDao {
    @Query("SELECT * FROM scrapped WHERE url = :url")
    fun getByUrl(url: String) : Scrapped?

    @Insert
    fun insert(scrapped: Scrapped)
}