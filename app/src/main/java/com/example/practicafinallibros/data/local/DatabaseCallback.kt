package com.example.practicafinallibros.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.practicafinallibros.data.remote.RetrofitClient
import com.example.practicafinallibros.data.repository.OpenLibraryRepository
import kotlinx.coroutines.runBlocking

class DatabaseCallback : RoomDatabase.Callback() {

    private val repository = OpenLibraryRepository(RetrofitClient.booksApi)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        populateDatabase(db)
    }

    private fun populateDatabase(db: SupportSQLiteDatabase) {
        runBlocking {
            val defaultBooks = repository.getDefaultBooks()
            defaultBooks.forEach { book ->
                val sql = """
                    INSERT INTO books (title, author, description, imageUri, createdAt, createdBy, createdByName, genre, pageCount, publishYear, isFavorite)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                """.trimIndent()
                db.execSQL(sql, arrayOf(
                    book.title,
                    book.author,
                    book.description,
                    book.imageUri,
                    book.createdAt,
                    book.createdBy,
                    book.createdByName,
                    book.genre,
                    book.pageCount,
                    book.publishYear
                ))
            }
        }
    }
}
