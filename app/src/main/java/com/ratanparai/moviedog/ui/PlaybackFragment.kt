package com.ratanparai.moviedog.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackGlue
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.player.MediaSessionCallback
import com.ratanparai.moviedog.player.VideoPlayerGlue
import com.ratanparai.moviedog.service.MovieService
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID

class PlaybackFragment: VideoSupportFragment() {
    private val TAG = "PlaybackFragment"

    private val playWhenReadyPlayerCallback: PlaybackGlue.PlayerCallback = PlayWhenReadyPlayerCallback()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment loaded")

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)

        if(movieId == -1) {
            Log.w(TAG, "Invalid movieId, cannot playback.")
            throw IllegalArgumentException("Invalid movieId $movieId")
        }

        val mediaSession = MediaSessionCompat(context, TAG)
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.isActive = true

        MediaControllerCompat.setMediaController(context as Activity, mediaSession.controller)

        // val playerGlue = PlaybackTransportControlGlue(activity, MediaPlayerAdapter(activity))
        val playerGlue = VideoPlayerGlue(context!!, MediaPlayerAdapter(context), mediaSession.controller)

        playerGlue.host = VideoSupportFragmentGlueHost(this)
        playerGlue.addPlayerCallback(playWhenReadyPlayerCallback)

        /**
         * Delegates media commands sent from the assistant to the glue. <br>
         * Note that Play/Pause are handled via key code input, not MediaSession.
         */
        // TODO: MediaSessionCallback

        val mediaSessionCallback = MediaSessionCallback(playerGlue, activity!!)
        mediaSession.setCallback(mediaSessionCallback)


        val movieService = MovieService(context!!)


        val movie = movieService.getMovieById(movieId!!)


        playMedia(playerGlue, movie)

    }

    private fun playMedia(
        playerGlue: VideoPlayerGlue<MediaPlayerAdapter>,
        movie: Movie
    ) {
        Log.d(TAG, "Streaming URL: ${movie.videoUrl}")
        playerGlue.subtitle = movie.description
        playerGlue.title = movie.title
        playerGlue.playerAdapter.setDataSource(Uri.parse(movie.videoUrl))
    }

    private class PlayWhenReadyPlayerCallback : PlaybackGlue.PlayerCallback() {
        override fun onPreparedStateChanged(glue: PlaybackGlue) {
            super.onPreparedStateChanged(glue)
            if (glue.isPrepared) {
                glue.play()
            }
        }
    }
}