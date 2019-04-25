package com.ratanparai.moviedog.service

import android.content.Context
import android.util.Log
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.scrapper.DekhvhaiScrapper
import java.lang.Exception

class MovieService(private val context: Context) {

    fun search(query: String): List<Movie> {
        val scrapper = DekhvhaiScrapper()
        val searchUrl = scrapper.getSearchUrl(query)
        val document = scrapper.getDocument(searchUrl)
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(document)

        val movieDao = AppDatabase.getInstance(context).movieDao()

        for (link in movieLinks) {
            val movieDoc = scrapper.getDocument(link)
            val movie = scrapper.getMovie(movieDoc)
            try {
                movieDao.insertMovie(movie)
            } catch (ex: Exception) {
                Log.d("MovieService", ex.message)
            }

        }

        return movieDao.searchByTitle(query)
    }
}