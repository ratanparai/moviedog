package com.ratanparai.moviedog.ui

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.leanback.app.VideoFragmentGlueHost
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.service.MovieService
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID

class PlaybackFragment: VideoSupportFragment() {
    private val TAG = "PlaybackFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment loaded")

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)

        if(movieId == -1) {
            Log.w(TAG, "Invalid movieId, cannot playback.")
            throw IllegalArgumentException("Invalid movieId $movieId")
        }

        val playerGlue = PlaybackTransportControlGlue(activity, MediaPlayerAdapter(activity))
        playerGlue.host = VideoSupportFragmentGlueHost(this)


        val movieService = MovieService(context!!)


        val movie = movieService.getMovieById(movieId!!)


        playerGlue.subtitle = movie.description
        playerGlue.title = movie.title
        playerGlue.playerAdapter.setDataSource(Uri.parse(movie.videoUrl))
        playerGlue.play()

    }
}