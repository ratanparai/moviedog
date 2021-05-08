package com.ratanparai.moviedog.service

import android.content.Context
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.masterwok.opensubtitlesandroid.OpenSubtitlesUrlBuilder
import com.masterwok.opensubtitlesandroid.models.OpenSubtitleItem
import com.masterwok.opensubtitlesandroid.services.OpenSubtitlesService
import com.ratanparai.moviedog.db.AppDatabase
import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.db.entity.Subtitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class SubtitleService(
    private val context: Context) {

    private val _tag = "SubtitleService"
    val subtitleDao = AppDatabase.getInstance(context).subtitleDao()

    fun downloadSubtitle(movie: Movie): List<Uri>? {
        val subtitles = subtitleDao.getSubtitles(movie.id)
        if(subtitles.isNotEmpty())
        {
            Log.d(_tag, "Loaded ${subtitles.size} from database")
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
            tryInsertIntoDb(subtitleToStore)
            return subtitleUrls
        } catch (e: Exception){
            Log.e(_tag, e.message, e)
        }
        return null
    }

    private fun tryInsertIntoDb(subtitleToStore: List<Subtitle>) {
        subtitleToStore.forEach { s ->
            run {
                try {
                    subtitleDao.insertSubtitle(s)
                } catch (e: Exception) {
                    Log.e(_tag, e.message, e)
                }
            }
        }
    }

    fun backgroundSubtitleDownload(movie: Movie){
        GlobalScope.launch(Dispatchers.IO) {
            downloadSubtitle(movie)
        }
    }
}