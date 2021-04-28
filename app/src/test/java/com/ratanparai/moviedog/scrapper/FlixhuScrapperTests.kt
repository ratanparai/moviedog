package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test

class FlixhuScrapperTests {
    private lateinit var searchDoc: Document
    private lateinit var movieDoc: Document

    @Before
    fun loadHtml() {
        val searchResultFile = ClassLoader.getSystemResource("Flixhub/search_harry_potter.data").readText()
        val deathlyHalosFile = ClassLoader.getSystemResource("Flixhub/details_harry_potter_chamer.data").readText()

        searchDoc = Jsoup.parse(searchResultFile, "http://172.27.27.84")
        movieDoc = Jsoup.parse(deathlyHalosFile, "http://172.27.27.84")
    }

    @Test
    fun shouldGetMovieLinksFromSearchResult() {
        val scrapper = FlixhubScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(searchDoc)

        Truth.assertThat(movieLinks.size).isEqualTo(4)

    }

    @Test
    fun shouldGetImdbIdFromSingleMovieDocument() {
        val scrapper = FlixhubScrapper()

        val actual = scrapper.getImdbId(movieDoc)
        val expected = "tt0295297"

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldGetMovieFromUrl() {
        val scrapper = FlixhubScrapper()
        val movie = scrapper.getMovie(movieDoc)

        Truth.assertThat(movie).isNotNull()
        Truth.assertThat(movie.title).isEqualTo("Harry Potter and the Chamber of Secrets")
        Truth.assertThat(movie.productionYear).isEqualTo(2002)
        Truth.assertThat(movie.duration).isEqualTo(9660000)
        Truth.assertThat(movie.videoUrl).isEqualTo("http://45.123.41.54/Data/Disk1/Hollywood/2002/Harry%20Potter%20and%20the%20Chamber%20of%20Secrets%20(2002)/Harry%20Potter%20and%20the%20Chamber%20of%20Secrets%20(2002).mkv")
        Truth.assertThat(movie.description).isEqualTo("Cars fly, trees fight back, and a mysterious house-elf comes to warn Harry Potter at the start of his second year at Hogwarts. Adventure and danger await when bloody writing on a wall announces: The Chamber Of Secrets Has Been Opened. To save Hogwarts will require all of Harry, Ron and Hermione’s magical abilities and courage.")

    }
}