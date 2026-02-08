package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.remote.BooksApi
import com.example.practicafinallibros.data.remote.dto.OpenLibraryBookDto

class OpenLibraryRepository(
    private val api: BooksApi
) {

    suspend fun searchBooks(query: String, limit: Int = 20): List<OpenLibraryBookDto> {
        val response = api.searchBooks(query = query, limit = limit)
        return response.docs
    }
}
