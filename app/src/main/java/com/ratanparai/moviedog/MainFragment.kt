package com.ratanparai.moviedog

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.SectionRow

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        setupUi()
        setupRowAdapter()
    }

    private fun setupRowAdapter() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter

        Handler().postDelayed({
            createRows()
            startEntranceTransition()
        }, 500)
    }

    private fun createRows() {

        rowsAdapter.add(SectionRow(HeaderItem("Continue Watching")))

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
