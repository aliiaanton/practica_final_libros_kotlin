package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.local.dao.BookDao
import com.example.practicafinallibros.data.local.entity.BookEntity

class BookRepository(
    private val bookDao: BookDao
) {

    fun observeAll() = bookDao.getAllBooks()

    fun observeByUser(userId: String) = bookDao.getBooksByUser(userId)

    fun observeFavorites() = bookDao.getFavoriteBooks()

    fun observeSearch(query: String) = bookDao.searchBooks(query)

    suspend fun insert(book: BookEntity) {
        bookDao.insertBook(book)
    }

    suspend fun update(book: BookEntity) {
        bookDao.updateBook(book)
    }

    suspend fun delete(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun getById(bookId: Int): BookEntity? {
        return bookDao.getBookById(bookId)
    }

    suspend fun toggleFavoriteStatus(bookId: Int, isFavorite: Boolean) {
        bookDao.updateFavoriteStatus(bookId, isFavorite)
    }
}
