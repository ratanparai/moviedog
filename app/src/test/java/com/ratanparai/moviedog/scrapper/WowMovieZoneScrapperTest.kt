package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class WowMovieZoneScrapperTest {

    private lateinit var searchDoc: Document
    private lateinit var movieDoc: Document

    @Before
    fun loadHtml() {
        val searchResultFile = ClassLoader.getSystemResource("wow/HarryPotterSearch_wow.data").readText()
        val deathlyHalosFile = ClassLoader.getSystemResource("wow/HarryPotter_Deadly_Hallows_part2.data").readText()

        searchDoc = Jsoup.parse(searchResultFile, "http://172.27.27.84")
        movieDoc = Jsoup.parse(deathlyHalosFile, "http://172.27.27.84")
    }

    @Ignore
    @Test
    fun shouldGetHtmlWhenSearchPageRequested() {
        // Arrange
        var scrapper = WowMovieZoneScrapper()
        val expected = 1000

        // Act
        val searchResult = scrapper.getSearchResult("Harry Potter")
        val actual = searchResult.toString().length

        // Assert
        assertThat(actual).isGreaterThan(expected)

    }

    @Test
    fun shouldGetMovieLinksFromSearchResult() {
        val scrapper = WowMovieZoneScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(searchDoc)

        assertThat(movieLinks.size).isEqualTo(13)

    }

    @Test
    fun shouldGetImdbIdFromSingleMovieDocument() {
        val scrapper = WowMovieZoneScrapper()

        val actual = scrapper.getImdbId(movieDoc)
        val expected = "tt1201607"

        assertThat(actual).isEqualTo(expected)
    }
}