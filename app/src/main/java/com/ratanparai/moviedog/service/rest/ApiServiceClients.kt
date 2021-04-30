package com.ratanparai.moviedog.service.rest

class ApiServiceClients {
    companion object {
        private val BASE_URL = "https://www.omdbapi.com/"

        fun GetOmdbServiceClient(): OmdbServiceClient {
            return RetrofitClient.getClient(BASE_URL).create(OmdbServiceClient::class.java)
        }
    }

}