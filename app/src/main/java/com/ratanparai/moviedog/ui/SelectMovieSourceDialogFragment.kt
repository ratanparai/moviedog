package com.ratanparai.moviedog.ui

import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.MovieUrl
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID

class SelectMovieSourceDialogFragment: GuidedStepFragment() {

    private var movieUrls: List<MovieUrl>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)

        if (movieId == -1) {
            activity.finish()
        }

        movieUrls = AppDatabase.getInstance(context).movieUrlDao().getMovieUrlsByMovieId(movieId!!)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {


        val guidance =  GuidanceStylist.Guidance(
            getString(R.string.dialog_movie_select_title),
            getString(R.string.dialog_movie_select_description),
            "", null
        )

        return guidance
    }



    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        for (movieUrl in movieUrls!!) {
            actions.add(
                GuidedAction.Builder()
                    .id(movieUrl.id.toLong())
                    .description(movieUrl.movieUrl)
                    .title(movieUrl.serviceName).build()
            )
        }

//        var action = GuidedAction.Builder()
//            .id(ACTION_ID_POSITIVE)
//            .title("Positive").build()
//        actions.add(action)
//        action = GuidedAction.Builder()
//            .id(ACTION_ID_NEGATIVE)
//            .title("negative").build()
//        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
//        if (ACTION_ID_POSITIVE == action.id) {
//            Toast.makeText(
//                activity, "Positive Clicked",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else {
//            Toast.makeText(
//                activity, "Negative clicked",
//                Toast.LENGTH_SHORT
//            ).show()
//        }

        for (movieUrl in movieUrls!!) {
            if(movieUrl.id == action.id.toInt())
            startActivity(PlaybackActivity.createIntent(context, movieUrl.movieId, movieUrl.movieUrl))
        }
    }

    companion object {
        private const val ACTION_ID_POSITIVE = 1L
        private const val ACTION_ID_NEGATIVE = 2L
    }
}