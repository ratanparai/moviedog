package com.ratanparai.moviedog.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "searchHash", indices = [Index(value = ["url"], unique = true)])
data class SearchHash (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    val url: String = "",
    val md5hash : String = ""
)