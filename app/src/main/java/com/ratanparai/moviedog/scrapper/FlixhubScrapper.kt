package com.ratanparai.moviedog.scrapper

import com.ratanparai.moviedog.db.entity.Movie
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

class FlixhubScrapper : Scrapper {
    override fun getSearchUrl(query: String): String {
        return String.format(SEARCH_URL, query)
    }

    override fun getMovie(document: Document, url: String): Movie {
        val imdbId = getImdbId(document)

        var titleWithYear = document.select(".subheader-maintitle").text()
        var title = getOnlyTitleFromTitleAndYear(titleWithYear)
        var year = getOnlyYearFromTitleAndYear(titleWithYear).toInt()

        val description = document.select(".portfolio-item-desc-inner > p").text()

        val movieTime =
            document.select("#page_header > div.ph-content-wrap > div > div > div > div:nth-child(4) > table > tbody > tr:nth-child(6) > td:nth-child(2)").text()
        var duration = getDurationInMiliseconds(movieTime)

        var videoUrl = document.select("#page_header > div.ph-content-wrap > div > div > div > div:nth-child(4) > a").attr("href").replace(" ", "%20")

        val cardImage =
            document.select("#page_header > div.ph-content-wrap > div > div > div > div.col-md-2 > div > a > img")
                .attr("src")

        return Movie(
            title = title,
            description = description,
            imdbId = imdbId,
            videoUrl = videoUrl,
            productionYear = year,
            duration = duration,
            cardImage = cardImage
        )
    }

    override fun getListOfMovieLinksFromSearchResult(document: Document): List<String>{
        val result = ArrayList<String>()
        val elements = document.select("#tabs_i2-pane1 > div > a:first-child")
        for (elem in elements) {
            result.add(elem.attr("abs:href"))
        }

        return result
    }

    fun getImdbId(document: Document): String {
        val movieUrl =  document.select("body > meta:nth-child(9)").attr("content")

        return movieUrl.substring(movieUrl.indexOf("imdbid=") + 7, movieUrl.indexOf("&cat"))
    }


    fun getOnlyTitleFromTitleAndYear(titleWithyear: String): String {
        return titleWithyear.substring(0, titleWithyear.indexOf("(")).trim { it <= ' ' }
    }

    fun getOnlyYearFromTitleAndYear(titleWithYear: String): String {
        return titleWithYear.substring(titleWithYear.indexOf("(") + 1, titleWithYear.indexOf(")"))
    }

    fun getHourFromTime(time: String): Long {

        val stringHour = time.substring(0, time.indexOf("H")).trim { it <= ' ' }
        return stringHour.toLong()
    }

    fun getMinuteFromTime(time: String): Long {
        val stringMinute = time.substring(time.indexOf("H") + 1, time.indexOf("M")).trim { it <= ' ' }
        return stringMinute.toLong()
    }

    fun getDurationInMiliseconds(time: String): Int{
        return getDurationInMiliseconds(getHourFromTime(time), getMinuteFromTime(time))
    }

    fun getDurationInMiliseconds(hour: Long, minute: Long): Int{
        return (TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute)).toInt()
    }

    companion object{
        private const val SEARCH_URL = "http://www.flixhub.net/msearch.php?q=&searchquery=%s&q=M";
    }
}