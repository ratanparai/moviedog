package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test

class FtpMediaScrapperTests {
    private lateinit var bdPlex: Document
    private lateinit var deathlyHalos: Document

    @Before
    fun loadHtml() {
        val searchResultFile = ClassLoader.getSystemResource("FtpMedia/search_spider_man.data").readText()
        val deathlyHalosFile = ClassLoader.getSystemResource("FtpMedia/details_spider_man_far_from_home.data").readText()

        bdPlex = Jsoup.parse(searchResultFile, "http://10.1.1.1/")
        deathlyHalos = Jsoup.parse(deathlyHalosFile, "http://10.1.1.1/")
    }

    @Test
    fun shouldGetCorrectTitleFromTitleAndYear() {
        // Arrange
        val dekhvhaiScrapper = FtpMediaScrapper()
        val expected = "How to Train Your Dragon: The Hidden World"

        // Act
        val actual = dekhvhaiScrapper
            .getOnlyTitleFromTitleAndYear("How to Train Your Dragon: The Hidden World (2019)")

        // Assert
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldGetCorrectYearFromTitleAndYear() {
        // Arrange
        val dekhvhaiScrapper = FtpMediaScrapper()
        val expected = "2019"

        // Act
        val actual = dekhvhaiScrapper
            .getOnlyYearFromTitleAndYear("How to Train Your Dragon: The Hidden World (2019)")

        // Assert
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldReturnCorrectHour() {
        val dekhvhaiProvider = FtpMediaScrapper()
        val actual = dekhvhaiProvider.getHourFromTime("02 H 00 M")
        val expected = 2

        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun shouldReturnCorrectMinute() {
        val dekhvhaiProvider = FtpMediaScrapper()
        val actual = dekhvhaiProvider.getMinuteFromTime("02 H 15 M")
        val expected = 15

        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun shouldLoadHtmlFileAsJsoupDocument() {
        //val file = File("html/HarryPotter_Dekhvhai.data")
        val file = ClassLoader.getSystemResource("HarryPotter_Dekhvhai.data").readText()

        val document = Jsoup.parse(file, "UTF-8")

        Truth.assertThat(document).isNotNull()
    }

    @Test
    fun getMovieUrlsToBrowse() {
        val scrapper = FtpMediaScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(bdPlex)
        Truth.assertThat(movieLinks.size).isEqualTo(7)

        Truth.assertThat(movieLinks[0]).isEqualTo("http://10.1.1.1/movie.php?imdbid=tt0145487&cat=Hollywood")

    }

    @Test
    fun shouldGetMovieFromUrl() {
        val scrapper = FtpMediaScrapper()
        val movie = scrapper.getMovie(deathlyHalos, "")

        Truth.assertThat(movie).isNotNull()
        Truth.assertThat(movie.title).isEqualTo("Spider-Man: Homecoming")
        Truth.assertThat(movie.productionYear).isEqualTo(2017)
        Truth.assertThat(movie.duration).isEqualTo(7980000)
        Truth.assertThat(movie.videoUrl).isEqualTo("http://10.1.1.1/Data/English/2017/Spider%20Man%20Homecoming%20(2017)/Spider%20Man%20Homecoming%20(2017)1080p.mkv")
        Truth.assertThat(movie.description).isEqualTo("Following the events of Captain America: Civil War, Peter Parker, with the help of his mentor Tony Stark, tries to balance his life as an ordinary high school student in Queens, New York City, with fighting crime as his superhero alter ego Spider-Man as a new threat, the Vulture, emerges.")

    }

    @Test
    fun shouldGetImdbIdFromDocument() {
        val scrapper = FtpMediaScrapper()

        val actual = scrapper.getImdbId(deathlyHalos)
        val expected = "tt2250912"

        Truth.assertThat(actual).isEqualTo(expected)
    }
}