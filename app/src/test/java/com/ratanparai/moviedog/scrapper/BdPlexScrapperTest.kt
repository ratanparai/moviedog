package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
import org.junit.Test

class BdPlexScrapperTest {

    private lateinit var bdPlex: Document
    private lateinit var deathlyHalos: Document

    @Before
    fun loadHtml() {
        val searchResultFile = ClassLoader.getSystemResource("bdplex/Harry_Potter_bdplex.data").readText()
        val deathlyHalosFile = ClassLoader.getSystemResource("bdplex/deathly_hallows_bdplex.data").readText()

        bdPlex = Jsoup.parse(searchResultFile, "http://bdplex.net")
        deathlyHalos = Jsoup.parse(deathlyHalosFile, "http://bdplex.net")
    }

    @Test
    fun shouldGetCorrectTitleFromTitleAndYear() {
        // Arrange
        val dekhvhaiScrapper = BdPlexScrapper()
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
        val dekhvhaiScrapper = BdPlexScrapper()
        val expected = "2019"

        // Act
        val actual = dekhvhaiScrapper
            .getOnlyYearFromTitleAndYear("How to Train Your Dragon: The Hidden World (2019)")

        // Assert
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldReturnCorrectHour() {
        val dekhvhaiProvider = BdPlexScrapper()
        val actual = dekhvhaiProvider.getHourFromTime("02 H 00 M")
        val expected = 2

        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun shouldReturnCorrectMinute() {
        val dekhvhaiProvider = BdPlexScrapper()
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
        val scrapper = BdPlexScrapper()
        val movieLinks = scrapper.getListOfMovieLinksFromSearchResult(bdPlex)
        Truth.assertThat(movieLinks.size).isEqualTo(8)

        Truth.assertThat(movieLinks[0]).isEqualTo("http://bdplex.net/movie.php?imdbid=tt1201607&cat=Hollywood")

    }

    @Test
    fun shouldGetMovieFromUrl() {
        val scrapper = BdPlexScrapper()
        val movie = scrapper.getMovie(deathlyHalos)

        Truth.assertThat(movie).isNotNull()
        Truth.assertThat(movie.title).isEqualTo("Harry Potter and the Deathly Hallows: Part 2")
        Truth.assertThat(movie.productionYear).isEqualTo(2011)
        Truth.assertThat(movie.duration).isEqualTo(7800000)
        Truth.assertThat(movie.videoUrl).isEqualTo("http://bdplex.net/disk02/Movies/Hollywood/2011_2015/Harry%20Potter%20and%20the%20Deathly%20Hallows%20Part%202%20(2011)/Harry.Potter.And.The.Deathly.Hallows.Part.2.2011__BRrip__BDPLEX.mp4")
        Truth.assertThat(movie.description).isEqualTo("Harry, Ron and Hermione continue their quest to vanquish the evil Voldemort once and for all. Just as things begin to look hopeless for the young wizards, Harry discovers a trio of magical objects that endow him with powers to rival Voldemort's formidable skills.")

    }

    @Test
    fun shouldGetImdbIdFromDocument() {
        val scrapper = BdPlexScrapper()

        val actual = scrapper.getImdbId(deathlyHalos)
        val expected = "tt1201607"

        Truth.assertThat(actual).isEqualTo(expected)
    }
}