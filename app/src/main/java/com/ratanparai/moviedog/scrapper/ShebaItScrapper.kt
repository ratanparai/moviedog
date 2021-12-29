package com.ratanparai.moviedog.scrapper

class ShebaItScrapper: DekhvhaiScrapper() {
    val ShebaItSearchUrl = "http://103.195.1.50/msearch.php?q=&searchquery=%S&q=M"

    override fun getSearchUrl(query: String): String {
        return String.format(ShebaItSearchUrl, query)
    }
}