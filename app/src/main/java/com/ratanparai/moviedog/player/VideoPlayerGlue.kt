package com.ratanparai.moviedog.player

import android.app.Activity
import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.ratanparai.moviedog.service.MovieService
import com.ratanparai.moviedog.ui.PlaybackFragment
import java.util.concurrent.TimeUnit

class VideoPlayerGlue<T : PlayerAdapter>(
    context: Context,
    adapter: T,
    mediaController: MediaControllerCompat,
    val movieService: MovieService,
    val movieId: Int) :
        PlaybackTransportControlGlue<T>(context, adapter) {

    private val TAG = "VideoPlayerGlue"

    private val TEN_SECONDS = TimeUnit.SECONDS.toMillis(10)
    private val THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30)
    private val ONE_MINUTES = TimeUnit.MINUTES.toMillis(1)
    private val FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5)

    private val transportControls: MediaControllerCompat.TransportControls = mediaController.transportControls

    private val fastForwardAction: PlaybackControlsRow.FastForwardAction = PlaybackControlsRow.FastForwardAction(context)
    private val rewindAction: PlaybackControlsRow.RewindAction = PlaybackControlsRow.RewindAction(context)
    private val closedCaptionAction: PlaybackControlsRow.ClosedCaptioningAction = PlaybackControlsRow.ClosedCaptioningAction(context)


    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN) {

            val repeatCount = event.repeatCount

            val jumpTime = when {
                repeatCount>15 -> FIVE_MINUTES
                repeatCount > 10 -> ONE_MINUTES
                repeatCount > 5 -> THIRTY_SECONDS
                else -> TEN_SECONDS
            }

            return when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    Log.d(TAG, "Long Forward key pressed")
                    fastForward(jumpTime)
                    true
                }
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    Log.d(TAG, "Long Rewind key pressed")
                    rewind(jumpTime)
                    true
                }
                else -> super.onKey(v, keyCode, event)
            }
        }

        return super.onKey(v, keyCode, event)

    }

    override fun onPlayStateChanged() {
        super.onPlayStateChanged()
        if(isPlaying) {
            (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onUpdateProgress() {
        super.onUpdateProgress()
        movieService.updateMovieProgress(movieId, currentPosition)
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
        // Order matters, super.onCreatePrimaryActions() will create the play / pause action.
        // Will display as follows:
        // play/pause, previous, rewind, fast forward, next
        //   > /||      |<        <<        >>         >|
        super.onCreatePrimaryActions(primaryActionsAdapter)

        primaryActionsAdapter?.add(rewindAction)
        primaryActionsAdapter?.add(fastForwardAction)
        primaryActionsAdapter?.add(closedCaptionAction)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            rewindAction -> rewind()
            fastForwardAction -> fastForward()
            closedCaptionAction -> closedCaption()
            else -> super.onActionClicked(action) // Super class handles play/pause and delegates to abstract methods next()/previous().
        }
    }

    private fun closedCaption() {

    }

    private fun rewind(rewindTime: Long = TEN_SECONDS){
        var newPosition = currentPosition - rewindTime
        newPosition = if (newPosition < 0) 0 else newPosition

        // The MediaSession callback in PlaybackFragment will be triggered which will sync the glue
        // (this) with media session.
        transportControls.seekTo(newPosition)
    }

    private fun fastForward(forwardTime: Long = TEN_SECONDS) {
        if(duration> -1) {
            var newPosition = currentPosition + forwardTime
            newPosition = if(newPosition > duration) duration else newPosition

            // The MediaSession callback in PlaybackFragment will be triggered which will sync the glue
            // (this) with media session.
            transportControls.seekTo(newPosition)
        }
    }
}

