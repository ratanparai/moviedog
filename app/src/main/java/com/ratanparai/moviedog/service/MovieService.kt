package com.ratanparai.moviedog.service

import android.content.Context
import android.util.Log
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.db.entity.SearchHash
import com.ratanparai.moviedog.scrapper.DekhvhaiScrapper
import com.ratanparai.moviedog.utilities.MD5

class MovieService(private val context: Context) {

    fun search(query: String): List<Movie> {
        val scrapper = DekhvhaiScrapper()
        val searchUrl = scrapper.getSearchUrl(query)
        val document = scrapper.getDocument(searchUrl)

        val md5Hex = MD5(document.html())

        val searchHashDao = AppDatabase.getInstance(context).searchHashDao()
        val movieDao = AppDatabase.getInstance(context).movieDao()

        val sHash = searchHashDao.getByUrl(searchUrl)

        if (sHash?.md5hash == md5Hex) {
            // Already in the database
            return movieDao.searchByTitle(query)
        }


        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(document)


        for (link in movieLinks) {
            val movieDoc = scrapper.getDocument(link)
            val movie = scrapper.getMovie(movieDoc)
            try {
                movieDao.insertMovie(movie)
            } catch (ex: Exception) {
                Log.d("MovieService", ex.message)
            }

        }

        val searchHash = SearchHash(url = searchUrl, md5hash = md5Hex)
        
        if (sHash==null) {
            searchHashDao.add(searchHash)
        } else {
            searchHashDao.updateHash(sHash.id, md5Hex)
        }

        return movieDao.searchByTitle(query)
    }

    fun getMovieById(id : Int): Movie {
        val movieDao = AppDatabase.getInstance(context).movieDao()
        return movieDao.getMovieById(id)
    }
}