package com.ratanparai.moviedog.ui

import android.content.Context
import com.google.android.exoplayer2.upstream.ByteArrayDataSource
import com.google.android.exoplayer2.upstream.DataSource

class CustomDataSourceFactory(
    context: Context,
    var subtitles: ByteArray) : DataSource.Factory {
    override fun createDataSource(): DataSource {
        val dataSource = ByteArrayDataSource(subtitles)
        return dataSource
    }

}
