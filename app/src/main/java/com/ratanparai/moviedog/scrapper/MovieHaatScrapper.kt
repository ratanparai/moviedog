package com.ratanparai.moviedog.scrapper

import com.ratanparai.moviedog.db.entity.Movie
import com.ratanparai.moviedog.service.rest.ApiServiceClients
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

class MovieHaatScrapper : Scrapper {
    override fun getSearchUrl(query: String): String {
        return String.format(SEARCH_URL, query)
    }

    override fun getMovie(document: Document, url: String): Movie {

        val imdbId = getImdbId(url)

        var response = ApiServiceClients.GetOmdbServiceClient()
            .getMovieInfo("API_KEY", imdbId)
            .execute()
            .body()

        var duration = getDurationInMiliseconds(response!!.runtime)

        var videoUrl = document.select(".movie__download--btn > a").attr("href").replace(" ", "%20")


        return Movie(
            title = response.title,
            description = response.plot,
            imdbId = imdbId,
            videoUrl = videoUrl,
            productionYear = response.year,
            duration = duration,
            cardImage = response.poster
        )
    }

    override fun getListOfMovieLinksFromSearchResult(document: Document): List<String> {
        val result = ArrayList<String>()
        val elements = document.select("#tab-1 > div > div > div > div.card__cover > a")
        for (elem in elements) {
            result.add(elem.attr("abs:href"))
        }

        return result
    }

    fun getImdbId(url: String): String {
        var imdbId = url.substringAfterLast("/")
        return imdbId
    }

    fun getMinuteFromTime(time: String): Long {
        val stringMinute = time.substringBefore(" ")
        return stringMinute.toLong()
    }

    fun getDurationInMiliseconds(time: String): Int{
        return getDurationInMiliseconds(getMinuteFromTime(time))
    }

    fun getDurationInMiliseconds(minute: Long): Int{
        return TimeUnit.MINUTES.toMillis(minute).toInt()
    }

    companion object{
        const val SEARCH_URL = "http://www.moviehaat.net/search?q=%s"
    }
}