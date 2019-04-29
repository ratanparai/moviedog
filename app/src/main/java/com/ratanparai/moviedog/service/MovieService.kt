package com.ratanparai.moviedog.service

import android.content.Context
import android.util.Log
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.dao.MovieDao
import com.ratanparai.moviedog.db.dao.ScrappedDao
import com.ratanparai.moviedog.db.dao.SearchHashDao
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.db.entity.Scrapped
import com.ratanparai.moviedog.db.entity.SearchHash
import com.ratanparai.moviedog.scrapper.BdPlexScrapper
import com.ratanparai.moviedog.scrapper.DekhvhaiScrapper
import com.ratanparai.moviedog.scrapper.Scrapper
import com.ratanparai.moviedog.scrapper.WowMovieZoneScrapper
import com.ratanparai.moviedog.utilities.MD5

class MovieService(private val context: Context) {

    private val TAG = "MovieService"

    fun search(query: String): List<Movie> {
        val dekhvhaiScrapper = DekhvhaiScrapper()
        val bdPlexScrapper = BdPlexScrapper()
        val wowMovieZoneScrapper = WowMovieZoneScrapper()

        val searchHashDao = AppDatabase.getInstance(context).searchHashDao()
        val movieDao = AppDatabase.getInstance(context).movieDao()
        val scrappedDao = AppDatabase.getInstance(context).scrappedDao()

        scrapMovies(wowMovieZoneScrapper, query, searchHashDao, movieDao, scrappedDao)
        scrapMovies(bdPlexScrapper, query, searchHashDao, movieDao, scrappedDao)
        scrapMovies(dekhvhaiScrapper, query, searchHashDao, movieDao, scrappedDao)

        return movieDao.searchByTitle(query)
    }

    private fun scrapMovies(scrapper: Scrapper, query: String, searchHashDao: SearchHashDao, movieDao: MovieDao, scrappedDao: ScrappedDao) {
        try {
            val searchUrl = scrapper.getSearchUrl(query)
            val document = scrapper.getDocument(searchUrl)

            val md5Hex = MD5(document.html())

            val sHash = searchHashDao.getByUrl(searchUrl)

            if (sHash?.md5hash == md5Hex) {
                return
            }

            val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(document)

            Log.d(TAG, "Scrapping ${movieLinks.size} movies!")


            for (link in movieLinks) {

                if (alreadyScrappedMovie(link, scrappedDao)) {
                    Log.d(TAG, "Movie is already scrapped for URL: $link")
                    continue
                }

                Log.d(TAG, "First time scrapping movie for URL: $link")

                val scrapped = Scrapped(
                    url = link
                )

                val movieDoc = scrapper.getDocument(link)
                val movie = scrapper.getMovie(movieDoc)
                Log.d(TAG, "Scrapped movie: $movie for search URL $searchUrl ")
                try {
                    scrappedDao.insert(scrapped)
                    movieDao.insertMovie(movie)

                } catch (ex: Exception) {
                    Log.d("MovieService", ex.message)
                }

            }


            if (sHash == null) {
                val searchHash = SearchHash(url = searchUrl, md5hash = md5Hex)
                searchHashDao.add(searchHash)
            } else {
                searchHashDao.updateHash(sHash.id, md5Hex)
            }
        } catch (e: Exception){
            Log.e(TAG, e.message)
        }

    }

    private fun alreadyScrappedMovie(link: String, scrappedDao: ScrappedDao): Boolean {
        val scrapped = scrappedDao.getByUrl(link)
        if (scrapped != null) {
            return true
        }

        return false
    }

    fun getMovieById(id : Int): Movie {
        val movieDao = AppDatabase.getInstance(context).movieDao()
        return movieDao.getMovieById(id)
    }

    fun updateMovieProgress(id: Int, progress: Long) {
        // only update progress if there is any real progress
        if(progress > 0L) {
            val movieDao = AppDatabase.getInstance(context).movieDao()
            var lastPlayTime = System.currentTimeMillis()
            movieDao.updatePlayProgress(id, progress, lastPlayTime)
        }
    }
}