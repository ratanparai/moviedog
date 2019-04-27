package com.ratanparai.moviedog.scrapper

import com.ratanparai.moviedog.db.entity.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface Scrapper {
    fun getDocument(url: String): Document {
        return Jsoup.connect(url).get()
    }

    fun getDocument(url: String, headers: Map<String, String>): Document {
        return Jsoup.connect(url).headers(headers).get()
    }

    fun getSearchUrl(query: String): String

    fun getMovie(document: Document): Movie

    fun getListOfMovieLinksFromSearchResult(document: Document): List<String>
}