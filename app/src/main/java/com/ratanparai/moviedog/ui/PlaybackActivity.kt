package com.ratanparai.moviedog.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.ratanparai.moviedog.R
import com.ratanparai.moviedog.utilities.EXTRA_MOVIE_ID

class PlaybackActivity: FragmentActivity() {

    private val TAG = "PlaybackActivity"

    private lateinit var playbackFragment: PlaybackFragment

    override fun onStop() {
        Log.d(TAG, "Finishing Activity")
        super.onStop()
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val fragment = supportFragmentManager.findFragmentByTag(getString(R.string.playback_tag))
        if (fragment is PlaybackFragment) {
            playbackFragment = fragment
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "Key Pressed $keyCode")
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        fun createIntent(context: Context, movieId: Int): Intent {
            val intent = Intent(context, PlaybackActivity::class.java)
            intent.putExtra(EXTRA_MOVIE_ID, movieId)
            return intent
        }
    }
}