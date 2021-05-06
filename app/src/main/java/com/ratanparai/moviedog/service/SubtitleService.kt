package com.ratanparai.moviedog.service

import android.content.Context
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import com.masterwok.opensubtitlesandroid.OpenSubtitlesUrlBuilder
import com.masterwok.opensubtitlesandroid.models.OpenSubtitleItem
import com.masterwok.opensubtitlesandroid.services.OpenSubtitlesService
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.db.entity.Subtitle
import java.io.File
import java.net.URI

class SubtitleService(
    private val context: Context) {

    private val _tag = "SubtitleService"

    fun downloadSubtitle(movie: Movie): List<Uri>? {
        val subtitleDao = AppDatabase.getInstance(context).subtitleDao()
        val subtitles = subtitleDao.getSubtitles(movie.id)
        if(subtitles.isNotEmpty())
        {
            return subtitles.map { s -> Uri.parse(s.subtitleUrl) }
        }
        val imdbId = movie.imdbId.substringAfter("tt").toLong()
        val url = OpenSubtitlesUrlBuilder()
            .imdbId(imdbId)
            .subLanguageId("eng")
            .build()

        val subtitleUrls = ArrayList<Uri>()
        val service = OpenSubtitlesService()
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var searchResult: Array<OpenSubtitleItem> = service.search(OpenSubtitlesService.TemporaryUserAgent, url)

            if (searchResult.isEmpty())
            {
                val titleUrl = OpenSubtitlesUrlBuilder()
                    .query(movie.title)
                    .subLanguageId("eng")
                    .build()

                searchResult = service.search(OpenSubtitlesService.TemporaryUserAgent, titleUrl)
            }

            for(result in searchResult)
            {
                try {
                    if(subtitleUrls.size > 10)
                    {
                        break
                    }

                    if (result.SubFormat != "srt" || result.SubLanguageID != "eng")
                    {
                        continue
                    }

                    val subtitleFileName = result.IDSubtitleFile + "-" + result.SubFileName

                    val fileUri = Uri.fromFile(File(Uri.fromFile(context.cacheDir).path,subtitleFileName))

                    val file = File(URI.create(fileUri.toString()))
                    if(file.exists())
                    {
                        Log.d(_tag, "subtitle file $fileUri already exits in cache")
                        subtitleUrls.add(fileUri)
                        continue
                    }

                    service.downloadSubtitle(context, result, fileUri)
                    subtitleUrls.add(fileUri)
                    Log.d(_tag, "Successfully downloaded subtitle no. ${subtitleUrls.size}")

                } catch (e: Exception){
                    Log.e(_tag, e.message, e)
                }
            }
            val subtitleToStore = subtitleUrls.map { s -> Subtitle(movieId = movie.id, subtitleUrl = s.toString()) }
            subtitleDao.insertSubtitle(subtitleToStore)
            return subtitleUrls
        } catch (e: Exception){
            Log.e(_tag, e.message, e)
        }
        return null
    }
}