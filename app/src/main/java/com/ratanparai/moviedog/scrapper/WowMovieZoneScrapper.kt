package com.ratanparai.moviedog.scrapper

import com.ratanparai.moviedog.db.entity.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WowMovieZoneScrapper: Scrapper {
    override fun getSearchUrl(query: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMovie(document: Document): Movie {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListOfMovieLinksFromSearchResult(document: Document): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val SEARCH_URL = "http://172.27.27.84/ajax_search?search_value=%s"

    fun getSearchResult(query : String): Document? {
        var query = query.toLowerCase()
        val results = ArrayList<Movie>()

        val searchUrl = String.format(SEARCH_URL, query)

        return Jsoup.connect(searchUrl)
            .header("X-Requested-With", " XMLHttpRequest")
            .get()
    }
}