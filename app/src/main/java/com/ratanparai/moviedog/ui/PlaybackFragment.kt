package com.ratanparai.moviedog.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackTransportControlGlue
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.masterwok.opensubtitlesandroid.OpenSubtitlesUrlBuilder
import com.masterwok.opensubtitlesandroid.models.OpenSubtitleItem
import com.masterwok.opensubtitlesandroid.services.OpenSubtitlesService
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.player.VideoPlayerGlue
import com.ratanparai.moviedog.service.MovieService
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_URL
import java.io.File
import java.net.URI


class PlaybackFragment: VideoSupportFragment() {
    private val TAG = "PlaybackFragment"

    private lateinit var playerGlue: PlaybackTransportControlGlue<LeanbackPlayerAdapter>
    private lateinit var exoPlayer: SimpleExoPlayer

    private lateinit var movieService: MovieService

    private var movie: Movie? = null

    private var movieIdToPlay: Int = -1

    private var movieUrl: String? = null

    private var trackSelector: DefaultTrackSelector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment loaded")

        val movieId = activity?.intent?.getIntExtra(EXTRA_MOVIE_ID, -1)
        movieUrl = activity?.intent?.getStringExtra(EXTRA_MOVIE_URL)

        movieIdToPlay = movieId!!

        if(movieId == -1) {
            Log.w(TAG, "Invalid movieId, cannot playback.")
            throw IllegalArgumentException("Invalid movieId $movieId")
        }

        movieService = MovieService(context!!)

        movie = movieService.getMovieById(movieId)
    }



    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Starting player")
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "stopping player")
        if(playerGlue.isPlaying) {
            playerGlue.pause()
        }

        releasePlayer()
    }

    private fun initializePlayer() {
        val mediaSession = MediaSessionCompat(context!!, TAG).apply {
            isActive = true
            MediaControllerCompat.setMediaController(context as Activity, controller)
        }

        trackSelector = DefaultTrackSelector(context!!)

        trackSelector?.parameters = trackSelector!!
            .buildUponParameters()
            .setSelectUndeterminedTextLanguage(true)
            .setDisabledTextTrackSelectionFlags(C.SELECTION_FLAG_FORCED)
            .setRendererDisabled(2, false)
            .build()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context!!, trackSelector!!)

        MediaSessionConnector(mediaSession).apply {
            setPlayer(exoPlayer)
        }

        var subtitles = activity?.findViewById<SubtitleView>(R.id.leanback_subtitles)
        var textComponent = exoPlayer.textComponent

        if (subtitles != null && textComponent != null)
        {
            textComponent.addTextOutput(subtitles)
        }

        val playerAdapter = LeanbackPlayerAdapter(context!!, exoPlayer, 500)

        playerGlue = VideoPlayerGlue(
            context!!, 
            playerAdapter, 
            mediaSession.controller, 
            movieService, 
            movieIdToPlay,
            trackSelector!!)
        playerGlue.host = VideoSupportFragmentGlueHost(this)
        playerGlue.playWhenPrepared()
        playMedia(movie!!)
    }

    fun downloadSubtitle(): ArrayList<Uri>? {
        var imdbId = movie!!.imdbId.substringAfter("tt").toLong()
        val url = OpenSubtitlesUrlBuilder()
            .imdbId(imdbId)
            .subLanguageId("eng")
            .build()

        var subtitleUrls = ArrayList<Uri>()
        val service = OpenSubtitlesService()
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val searchResult: Array<OpenSubtitleItem> = service.search(OpenSubtitlesService.TemporaryUserAgent, url)

            for(result in searchResult)
            {
                try {
                    if(subtitleUrls.size > 3)
                    {
                        break
                    }

                    if (result.SubFormat != "srt" || result.SubLanguageID != "eng")
                    {
                        continue
                    }

                    var fileUri = Uri.fromFile(File(Uri.fromFile(context!!.cacheDir).path,result.SubFileName))

                    var file = File(URI.create(fileUri.toString()))
                    if(file.exists())
                    {
                        Log.d(TAG, "subtitle file $fileUri already exits in cache")
                        subtitleUrls.add(fileUri)
                        continue
                    }

                    service.downloadSubtitle(context!!, result, fileUri)
                    subtitleUrls.add(fileUri)
                    Log.d(TAG, "Succesfully downloaded subtitle no. ${subtitleUrls.size}")

                } catch (e: Exception){
                    Log.e(TAG, e.message, e)
                }
            }

            return subtitleUrls
        } catch (e: Exception){
            Log.e(TAG, e.message, e)
        }

        return null;
    }

    private fun playMedia(
        movie: Movie
    ) {
        Log.d(TAG, "Streaming URL: ${movie.videoUrl}")
        playerGlue.subtitle = movie.description
        playerGlue.title = movie.title

        prepareMediaForPlaying(movie)

        playerGlue.playerAdapter.seekTo(movie.progress)

        playerGlue.playWhenPrepared()
    }

    private fun prepareMediaForPlaying(movie: Movie) {
        val userAgent = Util.getUserAgent(context!!, "MovieDog")
        var dataSourceFactory = DefaultDataSourceFactory(context!!, userAgent)

        val mediaSource = if (movieUrl == null) {
            ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(movie.videoUrl))
        } else {
            ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(movieUrl!!))
        }

        var subtitleUris = downloadSubtitle()

        if(subtitleUris == null)
        {
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            return
        }

        var subtitleMediaSources = ArrayList<MediaSource>()
        for (subtitleUri in subtitleUris)
        {
            var subtitleByteArray = context!!.contentResolver.openInputStream(subtitleUri)?.buffered()?.use { it.readBytes() }

            var subtitleFormat = Format.createTextSampleFormat(
                null, MimeTypes.APPLICATION_SUBRIP, C.SELECTION_FLAG_FORCED, "en")
            var subtitleSource = SingleSampleMediaSource.Factory(
                CustomDataSourceFactory(
                    context!!,
                    subtitleByteArray!!))
                .createMediaSource(Uri.parse(""), subtitleFormat, C.TIME_UNSET)
            subtitleMediaSources.add(subtitleSource)
        }

        var mergingMediaSource = MergingMediaSource(mediaSource, *subtitleMediaSources.toTypedArray())

        exoPlayer.setMediaSource(mergingMediaSource)
        exoPlayer.prepare()
    }

    private fun releasePlayer() {
        exoPlayer.release()
    }

}