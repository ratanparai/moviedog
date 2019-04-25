package com.ratanparai.moviedog.scrapper

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface Scrapper {
    fun getDocument(url: String): Document {
        return Jsoup.connect(url).get()
    }

    fun getDocument(url: String, headers: Map<String, String>): Document {
        return Jsoup.connect(url).headers(headers).get()
    }
}