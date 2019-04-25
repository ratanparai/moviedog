package com.ratanparai.moviedog.scrapper

import com.ratanparai.moviedog.db.entity.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.concurrent.TimeUnit

class DekhvhaiScrapper: Scrapper {

    val DEKHVHAI_SEARCH_URL = "http://dekhvhai.com/msearch.php?q=&searchquery=%s&q=M"

    fun search(query: String): List<Movie> {

        var query = query.toLowerCase()
        val results = ArrayList<Movie>()

        val searchUrl = String.format(DEKHVHAI_SEARCH_URL, query)

        try {
            val document = Jsoup.connect(searchUrl).get()
            val elements = document.select("#tabs_i2-pane1 > div:nth-child(3)")
            val a = elements.select("a")
            var href = a.attr("href")

            href = href.replace(" ", "%20")

            href = "http://dekhvhai.com/$href"


            val document1 = Jsoup.connect(href).get()
            val select = document1.select("#sidebar-widget > div:nth-child(1) > div > div > a:nth-child(5)")


            val movieTitle =
                document1.select("#page_header > div.ph-content-wrap > div > div > div > div.col-md-3 > div.subheader-titles > h2")
                    .text()
            val description =
                document1.select("#page_header > div.ph-content-wrap > div > div > div > div.col-md-3 > div.portfolio-item-desc > div > p")
                    .text()
            val movieTime =
                document1.select("#sidebar-widget > div:nth-child(1) > div > div > table > tbody > tr:nth-child(6) > td:nth-child(2)")
                    .text()
            val cardImage =
                document1.select("#page_header > div.ph-content-wrap > div > div > div > div.col-md-2 > div > a > img")
                    .attr("src")


            // clean up everything
            val title = getOnlyTitleFromTitleAndYear(movieTitle)
            val year = getOnlyYearFromTitleAndYear(movieTitle)

            val yearInInt = Integer.parseInt(year)

            var duration = (TimeUnit.HOURS.toMillis(getHourFromTime(movieTime)) + TimeUnit.MINUTES.toMillis(getMinuteFromTime(movieTime))).toInt()


            val href1 = select.attr("href")
            println(href1)

            var movie = Movie(
                id = 1,
                cardImage = cardImage,
                description = description,
                duration = duration,
                productionYear = yearInInt,
                title = title,
                videoUrl = href1,
                imdbId = ""
            )

            results.add(movie)

        } catch (e: IOException) {
            e.printStackTrace()
        }



        return results

    }

    fun getMovie(document: Document): Movie {
        var titleWithYear = document.select(".subheader-maintitle").text()
        var title = getOnlyTitleFromTitleAndYear(titleWithYear)
        var year = getOnlyYearFromTitleAndYear(titleWithYear).toInt()

        val description = document.select(".portfolio-item-desc-inner > p").text()

        val movieTime =
            document.select("#sidebar-widget > div:nth-child(1) > div > div > table > tbody > tr:nth-child(6) > td:nth-child(2)").text()
        var duration = getDurationInMiliseconds(movieTime)

        var videoUrl = document.select("#sidebar-widget > div:nth-child(1) > div > div > a:nth-child(6)").attr("href")

        val cardImage =
            document.select("#page_header > div.ph-content-wrap > div > div > div > div.col-md-2 > div > a > img")
                .attr("src")

        return Movie(
            title = title,
            description = description,
            videoUrl = videoUrl,
            productionYear = year,
            duration = duration,
            cardImage = cardImage
        )
    }

    fun getListOfMovieLinksFromSearchResult(document: Document): List<String>{
        val result = ArrayList<String>()
        val elements = document.select("#tabs_i2-pane1 > div > a")
        for (elem in elements) {
            result.add(elem.attr("href"))
        }

        return result
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

}