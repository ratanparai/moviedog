package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test

class MovieHaatScrapperTests {
    private lateinit var searchDoc: Document
    private lateinit var movieDoc: Document

    @Before
    fun loadHtml() {
        val searchResultFile = ClassLoader.getSystemResource("MovieHaat/search_harry_potter.data").readText()
        val deathlyHalosFile = ClassLoader.getSystemResource("MovieHaat/details_harry_potter_chamber.data").readText()

        searchDoc = Jsoup.parse(searchResultFile, "http://www.moviehaat.net/")
        movieDoc = Jsoup.parse(deathlyHalosFile, "http://www.moviehaat.net/")
    }

    @Test
    fun shouldGetMovieLinksFromSearchResult() {
        val scrapper = MovieHaatScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(searchDoc)

        Truth.assertThat(movieLinks.size).isEqualTo(8)

    }

    @Test
    fun shouldParseMin() {
        var movieTime = "136 min"
        val scrapper = MovieHaatScrapper()
        var actual = scrapper.getDurationInMiliseconds(movieTime)

        Truth.assertThat(actual).isEqualTo(8160000)
    }

    @Test
    fun shouldGetMovieFromUrl() {
        val scrapper = MovieHaatScrapper()
        val movie = scrapper.getMovie(movieDoc, "http://www.moviehaat.net/movies/tt0295297")

        Truth.assertThat(movie).isNotNull()
        Truth.assertThat(movie.title).isEqualTo("Harry Potter and the Chamber of Secrets")
        Truth.assertThat(movie.productionYear).isEqualTo(2002)
        Truth.assertThat(movie.duration).isEqualTo(9660000)
        Truth.assertThat(movie.videoUrl).isEqualTo("http://cdn2.moviehaat.net:8080/Hollywood/Harry%20Potter%20and%20the%20Chamber%20of%20Secrets%20.mp4")
        Truth.assertThat(movie.description).isEqualTo("An ancient prophecy seems to be coming true when a mysterious presence begins stalking the corridors of a school of magic and leaving its victims paralyzed.")

    }
}