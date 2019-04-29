package com.ratanparai.moviedog.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.presenter.DetailsDescriptionPresenter
import com.ratanparai.moviedog.service.MovieService
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID

class MovieDetailsFragment: DetailsFragment() {

    private var movie: Movie? = null

    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var arrayAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)



        if (movieId != -1) {
            val movieService = MovieService(context)
            movie = movieService.getMovieById(movieId!!)

            presenterSelector = ClassPresenterSelector()
            arrayAdapter = ArrayObjectAdapter(presenterSelector)

            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            // setupRelatedMovieListRow()

            adapter = arrayAdapter

            // initializeBackground(movie)


        } else {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + movie?.toString())
        val row = DetailsOverviewRow(movie)
        val width = convertDpToPixel(context, DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(context, DETAIL_THUMB_HEIGHT)
        Glide.with(context)
            .load(movie?.cardImage)
            .centerCrop()
            .into<SimpleTarget<GlideDrawable>>(object : SimpleTarget<GlideDrawable>(width, height) {
                override fun onResourceReady(
                    resource: GlideDrawable,
                    glideAnimation: GlideAnimation<in GlideDrawable>
                ) {
                    Log.d(TAG, "details overview card image url ready: $resource")
                    row.imageDrawable = resource
                    arrayAdapter.notifyArrayItemRangeChanged(0, adapter.size())
                }
            })

        val actionAdapter = ArrayObjectAdapter()

        actionAdapter.add(
            Action(
                ACTION_PLAY_MOVIE,
                resources.getString(R.string.play_movie)
            )
        )

        val movieUrlDao = AppDatabase.getInstance(context).movieUrlDao()
        val movieUrlsByMovieId = movieUrlDao.getMovieUrlsByMovieId(movie!!.id)

        if (movieUrlsByMovieId.size > 1) {
            actionAdapter.add(
                Action(
                    ACTION_SELECT_SOURCE,
                    resources.getString(R.string.select_movie_source)
                )
            )
        }

        row.actionsAdapter = actionAdapter

        arrayAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor =
            ContextCompat.getColor(context, R.color.detail_view_background)
        detailsPresenter.actionsBackgroundColor =
            ContextCompat.getColor(context, R.color.detail_view_actionbar_background)

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            when {
                action.id == ACTION_PLAY_MOVIE -> startActivity(PlaybackActivity.createIntent(context, movie!!.id ))
                action.id == ACTION_SELECT_SOURCE -> {
                    val intent = Intent(context, SelectMovieSourceDialogActivity::class.java)
                    intent.putExtra(EXTRA_MOVIE_ID, movie!!.id)
                    startActivity(intent)
                }

                else -> Toast.makeText(context, action.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    companion object {
        private const val TAG = "MovieDetailsFragment"

        private const val ACTION_PLAY_MOVIE = 1L
        private const val ACTION_SELECT_SOURCE = 2L

        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 420
    }
}