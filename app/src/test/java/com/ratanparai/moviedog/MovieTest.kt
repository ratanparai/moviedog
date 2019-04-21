package com.ratanparai.moviedog

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovieTest {

    @Test
    fun setTitle_Should_Set_Title() {
        // Arrange
        var expected = "Harry Potter"
        var movie = Movie()

        // Act
        movie.title = "Harry Potter"
        var actual = movie.title

        // Assert
        assertThat(actual).isEqualTo(expected)
    }
}
