package com.ratanparai.moviedog.provider

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.dao.MovieDao
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.service.MovieService

class VideoProvider : ContentProvider() {

    private val TAG = "VideoContentProvider"


    private lateinit var movieDao: MovieDao

    private lateinit var movieService: MovieService

    private lateinit var uriMatcher: UriMatcher

    private val AUTHORITY = "com.ratanparai.moviedog"
    private val SEARCH_SUGGEST = 1

    private val queryProjection = arrayOf(
        BaseColumns._ID,
        SearchManager.SUGGEST_COLUMN_TEXT_1,
        SearchManager.SUGGEST_COLUMN_TEXT_2,
        SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE,
        SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR,
        SearchManager.SUGGEST_COLUMN_DURATION,
        SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
    )

    override fun onCreate(): Boolean {
        movieDao = AppDatabase.getInstance(context).movieDao()
        uriMatcher = buildUriMatcher()
        movieService = MovieService(context)

        return true
    }


    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, uri.toString())

        if (uriMatcher.match(uri) == SEARCH_SUGGEST) {
            Log.d(TAG, "Search suggestions requested.")

            return search(uri.lastPathSegment)

        } else {
            Log.d(TAG, "Unknown uri to query: $uri")
            throw IllegalArgumentException("Unknown Uri: $uri")
        }
    }

    private fun search(query: String?): Cursor? {
        if(query != null) {
            val movies = movieService.search(query)
            val matrixCursor = MatrixCursor(queryProjection)

            for (movie in movies) {
                matrixCursor.addRow(convertMovieIntoRow(movie))
            }
            return matrixCursor
        }
        return null
    }

    private fun convertMovieIntoRow(movie: Movie): Array<Any> {
        return arrayOf(
            movie.id,
            movie.title,
            movie.description,
            movie.cardImage,
            movie.productionYear,
            movie.duration,
            "GLOBALSEARCH",
            movie.id
        )
    }

    private fun buildUriMatcher(): UriMatcher {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(
            AUTHORITY, "/search/" + SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST
        )
        uriMatcher.addURI(
            AUTHORITY,
            "/search/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
            SEARCH_SUGGEST
        )
        return uriMatcher
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}