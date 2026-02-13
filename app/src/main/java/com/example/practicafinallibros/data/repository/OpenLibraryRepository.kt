package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.remote.BooksApi
import com.example.practicafinallibros.data.remote.dto.OpenLibraryBookDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class OpenLibraryRepository(
    private val api: BooksApi
) {

    suspend fun searchBooks(query: String, limit: Int = 20): List<OpenLibraryBookDto> {
        val response = api.searchBooks(query = query, limit = limit)
        return response.docs
    }

    suspend fun getDefaultBooks(): List<BookEntity> = coroutineScope {
        val queries = listOf(
            "Cien años de soledad Gabriel García Márquez",
            "1984 George Orwell",
            "El principito Antoine de Saint-Exupéry",
            "Don Quijote de la Mancha Miguel de Cervantes",
            "Orgullo y prejuicio Jane Austen"
        )

        queries.map { query ->
            async {
                try {
                    val response = api.searchBooks(query, 20)
                    response.docs.firstOrNull()?.let { book ->
                        BookEntity(
                            title = book.title ?: "Título desconocido",
                            author = book.authorName?.firstOrNull() ?: "Autor desconocido",
                            description = "Libro de la biblioteca Open Library.",
                            imageUri = book.coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
                            createdAt = System.currentTimeMillis(),
                            createdBy = "default",
                            createdByName = "Biblioteca",
                            genre = book.subject?.firstOrNull() ?: "General",
                            pageCount = book.numberOfPagesMedian ?: 0,
                            publishYear = book.firstPublishYear ?: 0
                        )
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }.mapNotNull { it.await() }
    }
}
