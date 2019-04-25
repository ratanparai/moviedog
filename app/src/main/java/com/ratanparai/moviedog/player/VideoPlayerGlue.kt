package com.ratanparai.moviedog.player

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import java.util.concurrent.TimeUnit

class VideoPlayerGlue<T : PlayerAdapter>(context: Context, adapter: T, mediaController: MediaControllerCompat) :
    PlaybackTransportControlGlue<T>(context, adapter) {

    private val TEN_SECONDS = TimeUnit.SECONDS.toMillis(10)

    private val transportControls: MediaControllerCompat.TransportControls = mediaController.transportControls

    private val fastForwardAction: PlaybackControlsRow.FastForwardAction = PlaybackControlsRow.FastForwardAction(context)
    private val rewindAction: PlaybackControlsRow.RewindAction = PlaybackControlsRow.RewindAction(context)


    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
        // Order matters, super.onCreatePrimaryActions() will create the play / pause action.
        // Will display as follows:
        // play/pause, previous, rewind, fast forward, next
        //   > /||      |<        <<        >>         >|
        super.onCreatePrimaryActions(primaryActionsAdapter)

        primaryActionsAdapter?.add(rewindAction)
        primaryActionsAdapter?.add(fastForwardAction)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            rewindAction -> rewind()
            fastForwardAction -> fastForward()
            else -> super.onActionClicked(action) // Super class handles play/pause and delegates to abstract methods next()/previous().
        }
    }

    private fun rewind(){
        var newPosition = currentPosition - TEN_SECONDS
        newPosition = if (newPosition < 0) 0 else newPosition

        // The MediaSession callback in PlaybackFragment will be triggered which will sync the glue
        // (this) with media session.
        transportControls.seekTo(newPosition)
    }

    private fun fastForward() {
        if(duration> -1) {
            var newPosition = currentPosition + TEN_SECONDS
            newPosition = if(newPosition > duration) duration else newPosition

            // The MediaSession callback in PlaybackFragment will be triggered which will sync the glue
            // (this) with media session.
            transportControls.seekTo(newPosition)
        }
    }
}

