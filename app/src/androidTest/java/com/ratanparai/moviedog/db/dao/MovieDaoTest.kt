package com.ratanparai.moviedog.db.dao

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.ratanparai.moviedog.Movie
import com.ratanparai.moviedog.db.AppDatabase
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class MovieDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao

    @Before
    fun createDb() {
        var context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        movieDao = database.movieDao()

        seedDatabase()
    }

    @After
    fun closeDb() {

    }

    @Test
    fun shouldRunTestInMobile() {
        // Arrange
        var expected = "Harry Potter"
        var movie = Movie()

        // Act
        movie.title = "Harry Potter"
        var actual = movie.title

        // Assert
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun shouldBeAbleToInsertDataIntoDatabase() {
        var movie = com.ratanparai.moviedog.db.entity.Movie(
            "2",
            "Spiderman",
            "description",
            "t5679",
            "http",
            "http",
            2019,
            1400
        )
        movieDao.insertMovie(movie)

        assertThat(movieDao.getMovies().size, equalTo(2))

        assertThat(movieDao.getMovieById("2").title, equalTo("Spiderman"))
    }

    @Test
    fun shouldGetMovieWithImdbId(){
        assertThat(movieDao.getMovieByImdbId("t5678").title, equalTo("Harry Potter"))
    }

    private fun seedDatabase() {
        var movie = com.ratanparai.moviedog.db.entity.Movie(
            "1",
            "Harry Potter",
            "description",
            "t5678",
            "http",
            "http",
            2019,
            1400
        )
        movieDao.insertMovie(movie)
    }
}