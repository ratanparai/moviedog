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
    private lateinit var playerGlue: VideoPlayerGlue<MediaPlayerAdapter>

    private var movieIdToPlay: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment loaded")

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)

        movieIdToPlay = movieId!!

        if(movieId == -1) {
            Log.w(TAG, "Invalid movieId, cannot playback.")
            throw IllegalArgumentException("Invalid movieId $movieId")
        }

        val movieService = MovieService(context!!)


        val movie = movieService.getMovieById(movieId!!)

        val mediaSession = MediaSessionCompat(context, TAG)
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.isActive = true

        MediaControllerCompat.setMediaController(context as Activity, mediaSession.controller)

        // val playerGlue = PlaybackTransportControlGlue(activity, MediaPlayerAdapter(activity))
        playerGlue = VideoPlayerGlue(context!!, MediaPlayerAdapter(context), mediaSession.controller, movie.progress)

        playerGlue.host = VideoSupportFragmentGlueHost(this)
        // playerGlue.addPlayerCallback(playWhenReadyPlayerCallback)

        /**
         * Delegates media commands sent from the assistant to the glue. <br>
         * Note that Play/Pause are handled via key code input, not MediaSession.
         */
        // TODO: MediaSessionCallback

        val mediaSessionCallback = MediaSessionCallback(playerGlue, activity!!)
        mediaSession.setCallback(mediaSessionCallback)





        playMedia(movie)

    }

    fun getCurrentProgress(): Long {
        return playerGlue.currentPosition
    }

    fun getMovieId(): Int {
        return movieIdToPlay
    }

    private fun playMedia(
        movie: Movie
    ) {
        Log.d(TAG, "Streaming URL: ${movie.videoUrl}")
        playerGlue.subtitle = movie.description
        playerGlue.title = movie.title

        playerGlue.playerAdapter.setDataSource(Uri.parse(movie.videoUrl))
        playerGlue.seekTo(movie.progress)
        playerGlue.playerAdapter.seekTo(movie.progress)

        playerGlue.playWhenPrepared()
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