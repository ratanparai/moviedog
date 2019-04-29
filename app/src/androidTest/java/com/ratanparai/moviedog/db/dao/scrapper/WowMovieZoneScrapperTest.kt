package com.ratanparai.moviedog.db.dao.scrapper

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.ratanparai.moviedog.scrapper.WowMovieZoneScrapper
import org.hamcrest.CoreMatchers.equalTo
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Before
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

    @Test
    fun shouldGetMovieFromMovieDocument() {
        val scrapper = WowMovieZoneScrapper()

        val movie = scrapper.getMovie(movieDoc)

        assertThat(movie.title, equalTo("Harry Potter And The Deathly Hallows Part 2"))
        assertThat(movie.description, equalTo("Harry, Ron, and Hermione search for Voldemort''s remaining Horcruxes in their effort to destroy the Dark Lord as the final battle rages on at Hogwarts."))
        assertThat(movie.videoUrl, equalTo("http://172.27.27.251/2TB1/1080p/2011/Harry%20Potter%20And%20The%20Deathly%20Hallows%20Part%202%20%282011%29%20%5B1080p%5D/Harry%20Potter%20And%20The%20Deathly%20Hallows%20Part%202%20%282011%29%20%5B1080p%5D.mp4"))
        assertThat(movie.productionYear, equalTo(2011))

    }
}