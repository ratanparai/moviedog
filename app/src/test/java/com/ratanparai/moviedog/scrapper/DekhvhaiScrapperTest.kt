package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth.assertThat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test
import java.io.File

class DekhvhaiScrapperTest {

    private lateinit var dekhvhai: Document

    @Before
    fun loadHtml() {
        val file = ClassLoader.getSystemResource("HarryPotter_Dekhvhai.html").readText()

        dekhvhai = Jsoup.parse(file, "UTF-8")
    }

    @Test
    fun shouldGetCorrectTitleFromTitleAndYear() {
        // Arrange
        val dekhvhaiScrapper = DekhvhaiScrapper()
        val expected = "How to Train Your Dragon: The Hidden World"

        // Act
        val actual = dekhvhaiScrapper
            .getOnlyTitleFromTitleAndYear("How to Train Your Dragon: The Hidden World (2019)")

        // Assert
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldGetCorrectYearFromTitleAndYear() {
        // Arrange
        val dekhvhaiScrapper = DekhvhaiScrapper()
        val expected = "2019"

        // Act
        val actual = dekhvhaiScrapper
            .getOnlyYearFromTitleAndYear("How to Train Your Dragon: The Hidden World (2019)")

        // Assert
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldReturnCorrectHour() {
        val dekhvhaiProvider = DekhvhaiScrapper()
        val actual = dekhvhaiProvider.getHourFromTime("02 H 00 M")
        val expected = 2

        assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun shouldReturnCorrectMinute() {
        val dekhvhaiProvider = DekhvhaiScrapper()
        val actual = dekhvhaiProvider.getMinuteFromTime("02 H 15 M")
        val expected = 15

        assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun shouldLoadHtmlFileAsJsoupDocument() {
        //val file = File("html/HarryPotter_Dekhvhai.html")
        val file = ClassLoader.getSystemResource("HarryPotter_Dekhvhai.html").readText()

        val document = Jsoup.parse(file, "UTF-8")

        assertThat(document).isNotNull()
    }

    @Test
    fun getMovieUrlsToBrowse() {
        val scrapper = DekhvhaiScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(dekhvhai)
        assertThat(movieLinks.size).isEqualTo(4)

        assertThat(movieLinks[0]).isEqualTo("http://dekhvhai.com/movie.php?imdbid=tt1201607&cat=English%20Movie")

    }

}