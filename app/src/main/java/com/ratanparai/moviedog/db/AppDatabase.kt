package com.ratanparai.moviedog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ratanparai.moviedog.db.dao.*
import com.ratanparai.moviedog.db.entity.*
import com.ratanparai.moviedog.utilities.DATABASE_NAME


@Database(entities = [Movie::class, SearchHash::class, Scrapped::class, MovieUrl::class, Subtitle::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun searchHashDao(): SearchHashDao
    abstract fun scrappedDao(): ScrappedDao
    abstract fun movieUrlDao(): MovieUrlDao
    abstract fun subtitleDao(): SubtitleDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}