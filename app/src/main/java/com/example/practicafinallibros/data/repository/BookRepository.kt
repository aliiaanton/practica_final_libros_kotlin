package com.example.practicafinallibros.data.repository

import com.example.practicafinallibros.data.local.dao.BookDao
import com.example.practicafinallibros.data.local.dao.UserFavoriteDao
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.local.entity.UserFavoriteEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao,
    private val userFavoriteDao: UserFavoriteDao
) {

    fun observeAll() = bookDao.getAllBooks()

    fun observeByUser(userId: String) = bookDao.getBooksByUser(userId)

    fun observeFavorites(userId: String): Flow<List<BookEntity>> = userFavoriteDao.getFavoriteBooks(userId)

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

    suspend fun toggleFavoriteStatus(userId: String, bookId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            userFavoriteDao.addFavorite(UserFavoriteEntity(userId, bookId))
        } else {
            userFavoriteDao.removeFavorite(userId, bookId)
        }
    }
}
