package com.ratanparai.moviedog.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.dao.MovieDao
import com.ratanparai.moviedog.db.entity.Movie
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
        setupEventListeners()
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener(context)
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

        val movies = movieDao.getCurrentlyPlaying()

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)

        for (movie in movies) {
            listRowAdapter.add(movie)
        }

        val header = HeaderItem("Continue watching")

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


    private class ItemViewClickedListener(val context: Context) : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is Movie) {
                val movie = item as Movie

                val intent = PlaybackActivity.createIntent(context, movie.id)
                context.startActivity(intent)

            }

        }
    }
}

