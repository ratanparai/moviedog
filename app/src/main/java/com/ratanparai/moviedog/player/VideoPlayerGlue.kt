package com.ratanparai.moviedog.player

import android.app.Activity
import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.service.MovieService
import java.util.concurrent.TimeUnit


class VideoPlayerGlue<T : PlayerAdapter>(
    context: Context,
    adapter: T,
    mediaController: MediaControllerCompat,
    val movieService: MovieService,
    val movieId: Int,
    val trackSelector: DefaultTrackSelector) :
        PlaybackTransportControlGlue<T>(context, adapter) {

    private val TAG = "VideoPlayerGlue"

    private val TEN_SECONDS = TimeUnit.SECONDS.toMillis(10)
    private val THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30)
    private val ONE_MINUTES = TimeUnit.MINUTES.toMillis(1)
    private val FIVE_MINUTES = TimeUnit.MINUTES.toMillis(5)

    private var _firstTimePlay = true

    private val transportControls: MediaControllerCompat.TransportControls = mediaController.transportControls

    private val fastForwardAction: PlaybackControlsRow.FastForwardAction = PlaybackControlsRow.FastForwardAction(context)
    private val rewindAction: PlaybackControlsRow.RewindAction = PlaybackControlsRow.RewindAction(context)
    private val closedCaptionAction: PlaybackControlsRow.ClosedCaptioningAction = PlaybackControlsRow.ClosedCaptioningAction(context)
    private val audioAction = PlaybackControlsRow.MoreActions(context)

    private var subtitleIndex: Int = -1
    private var audioIndex: Int = 0

    private var toast: Toast? = null

    init {
        audioAction.icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_audiotrack, null)
    }

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
        if(_firstTimePlay){
            if(closedCaption()){
                _firstTimePlay = false
            }

        }
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
        primaryActionsAdapter?.add(audioAction)
    }

    override fun onActionClicked(action: Action?) {
        when (action) {
            rewindAction -> rewind()
            fastForwardAction -> fastForward()
            closedCaptionAction -> closedCaption()
            audioAction -> ChangeAudio()
            else -> super.onActionClicked(action) // Super class handles play/pause and delegates to abstract methods next()/previous().
        }
    }

    private fun ChangeAudio(): Boolean {
        var mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var trackGroupArray: TrackGroupArray? = mappedTrackInfo?.getTrackGroups(1) ?: return false

        val langList: ArrayList<String> = ArrayList()

        var ix = -1
        var iy = -1
        run {
            ix = 0
            while (ix < trackGroupArray!!.length) {
                val tg: TrackGroup = trackGroupArray.get(ix)
                iy = 0
                while (iy < tg.length) {
                    val fmt = tg.getFormat(iy)
                    langList.add(fmt.language!!)
                    iy++
                }
                ix++
            }
        }

        val msg = StringBuilder()

        if(++audioIndex > langList.size-1)
        {
            audioIndex = 0
        }

        val lang = langList[audioIndex]

        trackSelector.parameters = trackSelector
            .buildUponParameters()
            .setPreferredAudioLanguage(lang)
            .build()

        msg.append("Audio changed")
        msg.append(" (").append(lang).append(")")


        if (toast != null) toast?.cancel()
        toast = Toast.makeText(
            context,
            msg, Toast.LENGTH_LONG
        )
        toast?.show()
        return true
    }

    private fun closedCaption(): Boolean {
        var mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var trackGroupArray: TrackGroupArray? = mappedTrackInfo?.getTrackGroups(2) ?: return false

        val langList: ArrayList<String> = ArrayList()

        var ix = -1
        var iy = -1
        run {
            ix = 0
            while (ix < trackGroupArray!!.length) {
                val tg: TrackGroup = trackGroupArray.get(ix)
                iy = 0
                while (iy < tg.length) {
                    val fmt = tg.getFormat(iy)
                    langList.add(fmt.language!!)
                    iy++
                }
                ix++
            }
        }


        val msg = StringBuilder()
        if (++subtitleIndex < langList.size) {

            val lang = langList[subtitleIndex]

            trackSelector.parameters = trackSelector
                .buildUponParameters()
                .setPreferredTextLanguage(lang)
                .setSelectUndeterminedTextLanguage(true)
                .setDisabledTextTrackSelectionFlags(C.SELECTION_FLAG_FORCED)
                .setRendererDisabled(2, false)
                .build()

            msg.append("Subtitle enabled.")
            if (langList.size > 1) msg.append(" (").append(subtitleIndex + 1).append(")")
        } else {
            subtitleIndex = -1
            trackSelector.parameters = trackSelector
                .buildUponParameters()
                .setRendererDisabled(2, true)
                .build()

            msg.append("Subtitle disabled.")
        }

        if (toast != null) toast?.cancel()
        toast = Toast.makeText(
            context,
            msg, Toast.LENGTH_LONG
        )
        toast?.show()
        return true
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

