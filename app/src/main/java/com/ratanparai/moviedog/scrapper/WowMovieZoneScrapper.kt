package com.ratanparai.moviedog.scrapper

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import com.ratanparai.moviedog.db.entity.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.HashMap

class WowMovieZoneScrapper: Scrapper {

    override fun getSearchUrl(query: String): String {
        return String.format(SEARCH_URL, query)
    }

    override fun getMovie(document: Document): Movie {
        // var titleWithYear = document.select(".subheader-maintitle").text()
        var title = document.select("body > section.section.details > div.container > div > div:nth-child(1) > h1").text()
        var yearText = document.select("body > section.section.details > div.container > div > div.col-10 > div > div > div.col-12.col-sm-8.col-md-8.col-lg-9.col-xl-9 > div > ul > li:nth-child(2)").text()

        var year = getYearFromYearText(yearText)

        val description = document.select("div.card__description").text()

        var videoUrl = document.select("video > source").attr("src").replace(" ", "%20")

        var duration = getDuration(videoUrl)

        val cardImage =
            document.select("div.card__cover > img")
                .attr("abs:src")

        return Movie(
            title = title,
            description = description,
            videoUrl = videoUrl,
            productionYear = year,
            duration = duration,
            cardImage = cardImage
        )
    }

    private fun getYearFromYearText(yearText: String): Int {
        val yearOnly = yearText.substring(yearText.indexOf(":")+1)
        return yearOnly.toInt()
    }

    override fun getListOfMovieLinksFromSearchResult(document: Document): List<String> {
        val result = ArrayList<String>()
        val elements = document.select("a")
        for (elem in elements) {
            result.add(elem.attr("abs:href"))
        }

        return result.distinct()
    }

    fun getSearchResult(query : String): Document? {
        var query = query.toLowerCase()
        val results = ArrayList<Movie>()

        val searchUrl = String.format(SEARCH_URL, query)

        return Jsoup.connect(searchUrl)
            .header("X-Requested-With", " XMLHttpRequest")
            .get()
    }

    fun getDuration(videoUrl: String): Int {
        val mmr = MediaMetadataRetriever()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mmr.setDataSource(videoUrl, HashMap<String, String>())
        } else {
            mmr.setDataSource(videoUrl)
        }
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
    }

    companion object {
        private const val SEARCH_URL = "http://172.27.27.84/ajax_search?search_value=%s"
    }
}