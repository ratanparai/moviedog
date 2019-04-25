package com.ratanparai.moviedog.player

import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.leanback.media.PlaybackTransportControlGlue

class MediaSessionCallback(val glue: PlaybackTransportControlGlue<*>, val activity: FragmentActivity): MediaSessionCompat.Callback() {

    private val TAG = "MediaSessionCallback"

    override fun onPrepare() {
        super.onPrepare()
        glue.seekTo(50000)
    }

    override fun onPlay() {
        Log.d(TAG, "MediaSessionCallback: onPlay()")
        glue.play()
    }

    override fun onPause() {
        Log.d(TAG, "MediaSessionCallback: pause()")
        glue.pause()
    }

    override fun onSeekTo(pos: Long) {
        Log.d(TAG, "MediaSessionCallback: onSeekTo()")
        glue.seekTo(pos)
    }

    override fun onStop() {
        Log.d(TAG, "MediaSessionCallback: onStop()")
        // In onDestroy(), the player will be released. If you are using ExoPlayer, you will
        // need to manually release the player.
        activity.finish()
    }
}