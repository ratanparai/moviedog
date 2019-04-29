package com.ratanparai.moviedog

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.core.content.IntentCompat.EXTRA_START_PLAYBACK
import com.ratanparai.moviedog.ui.MovieDetailsActivity
import com.ratanparai.moviedog.ui.PlaybackActivity

class SearchableActivity: Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Search data ${intent.data}")

        val uri = intent.data
        val id = uri.lastPathSegment.toInt()

        val startPlayback = intent.getBooleanExtra(EXTRA_START_PLAYBACK, false)

        if (startPlayback) {
            Log.d(TAG, "Starting playback")
            startActivity(PlaybackActivity.createIntent(this, id))
        } else {
            Log.d(TAG, "Show movie details screen")
            // startActivity(PlaybackActivity.createIntent(this, id))
            startActivity(MovieDetailsActivity.createIntent(this, id))
        }

        finish()
    }

    companion object {
        private const val TAG = "SearchableActivity"
    }
}