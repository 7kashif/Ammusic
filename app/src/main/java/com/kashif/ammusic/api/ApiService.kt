package com.kashif.ammusic.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/oembed")
    suspend fun getVideoDetails(
        @Query("url") url: String,
        @Query("format") format: String = "json"
    ): Response<VideoResponse>
}