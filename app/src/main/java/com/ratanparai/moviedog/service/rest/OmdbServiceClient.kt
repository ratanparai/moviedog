package com.ratanparai.moviedog.service.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OmdbServiceClient {
    @GET("/?apikey=API_KEY")
    fun getMovieInfo(
        @Query("i") imdbId: String)
            : Call<OmdbMovieInfo?>
}