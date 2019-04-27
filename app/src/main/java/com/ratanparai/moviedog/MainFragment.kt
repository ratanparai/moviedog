package com.ratanparai.moviedog

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.dao.MovieDao
import com.ratanparai.moviedog.presenter.CardPresenter

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    private lateinit var movieDao: MovieDao


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        movieDao = AppDatabase.getInstance(context).movieDao()

        setupUi()
        setupRowAdapter()
    }

    private fun setupRowAdapter() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        Handler().postDelayed({
            createRows()
            startEntranceTransition()
        }, 500)
    }

    private fun createRows() {

        // rowsAdapter.add(SectionRow(HeaderItem("Continue Watching")))

        val movies = movieDao.getMovies()

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)

        for (movie in movies) {
            listRowAdapter.add(movie)
        }

        val header = HeaderItem("All Movies")

        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter

    }

    private fun setupUi() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)

        setOnSearchClickedListener {
            Toast.makeText(
                activity, getString(R.string.not_implemented),
                Toast.LENGTH_LONG
            ).show()
        }

        prepareEntranceTransition()
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}
