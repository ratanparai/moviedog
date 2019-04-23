package com.ratanparai.moviedog.scrapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DekhvhaiScrapperTest {
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
}