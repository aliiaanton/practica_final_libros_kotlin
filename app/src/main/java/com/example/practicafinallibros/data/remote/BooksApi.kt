package com.example.practicafinallibros.data.remote

import com.example.practicafinallibros.data.remote.dto.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int
    ): OpenLibrarySearchResponse
}