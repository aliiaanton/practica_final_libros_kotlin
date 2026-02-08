package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.repository.BookRepository
import com.example.practicafinallibros.ui.state.BooksUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
): ViewModel() {

    var bookList by mutableStateOf<List<BookEntity>>(emptyList())
        private set

    var uiState by mutableStateOf<BooksUiState>(BooksUiState.Idle)
        private set

    var showOnlyMine by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var currentUserId: String = ""

    fun setCurrentUser(userId: String) {
        currentUserId = userId
        loadBooks()
    }

    fun toggleShowOnlyMine() {
        showOnlyMine = !showOnlyMine
        loadBooks()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val flow = when {
                searchQuery.isNotBlank() -> repository.observeSearch(searchQuery)
                showOnlyMine -> repository.observeByUser(currentUserId)
                else -> repository.observeAll()
            }
            flow.collectLatest { books ->
                bookList = books
            }
        }
    }

    fun addBook(book: BookEntity) {
        viewModelScope.launch {
            uiState = BooksUiState.Loading
            try {
                repository.insert(book)
                uiState = BooksUiState.Success("Libro añadido correctamente")
            } catch (e: Exception) {
                uiState = BooksUiState.Error("Error al añadir el libro")
            }
        }
    }

    fun updateBook(book: BookEntity) {
        viewModelScope.launch {
            uiState = BooksUiState.Loading
            try {
                repository.update(book)
                uiState = BooksUiState.Success("Libro actualizado correctamente")
            } catch (e: Exception) {
                uiState = BooksUiState.Error("Error al actualizar el libro")
            }
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            uiState = BooksUiState.Loading
            try {
                repository.delete(book)
                uiState = BooksUiState.Success("Libro eliminado correctamente")
            } catch (e: Exception) {
                uiState = BooksUiState.Error("Error al eliminar el libro")
            }
        }
    }

    suspend fun getBookById(bookId: Int): BookEntity? {
        return repository.getById(bookId)
    }

    fun resetState() {
        uiState = BooksUiState.Idle
    }
}
