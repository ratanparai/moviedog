package com.ratanparai.moviedog.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ratanparai.moviedog.db.entity.Subtitle

@Dao
interface SubtitleDao {
    @Insert
    fun insertSubtitle(subtitles: Subtitle)

    @Query("SELECT * FROM subtitle WHERE movieId = :id")
    fun getSubtitles(id: Int): List<Subtitle>
}