package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WowMovieZoneScrapperTest {

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
}