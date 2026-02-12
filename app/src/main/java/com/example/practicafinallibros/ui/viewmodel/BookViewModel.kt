package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.repository.BookRepository
import com.example.practicafinallibros.ui.state.BooksUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
): ViewModel() {

    var bookList by mutableStateOf<List<BookEntity>>(emptyList())
        private set

    var favoriteBooks by mutableStateOf<List<BookEntity>>(emptyList())
        private set

    var uiState by mutableStateOf<BooksUiState>(BooksUiState.Idle)
        private set

    private val _eventChannel = Channel<String>(Channel.BUFFERED)
    val eventFlow = _eventChannel.receiveAsFlow()

    var showOnlyMine by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var currentUserId: String = ""
    private var lastDeletedBook: BookEntity? = null

    fun setCurrentUser(userId: String) {
        currentUserId = userId
        loadBooks()
        loadFavorites()
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
            uiState = BooksUiState.Loading
            val flow = when {
                searchQuery.isNotBlank() -> repository.observeSearch(searchQuery)
                showOnlyMine -> repository.observeByUser(currentUserId)
                else -> repository.observeAll()
            }
            flow.collectLatest { books ->
                bookList = books
                uiState = BooksUiState.Idle
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.observeFavorites().collectLatest { favorites ->
                favoriteBooks = favorites
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
            lastDeletedBook = book
            try {
                repository.delete(book)
                _eventChannel.send("Libro eliminado")
            } catch (e: Exception) {
                uiState = BooksUiState.Error("Error al eliminar el libro")
            }
        }
    }

    fun undoDelete() {
        lastDeletedBook?.let { book ->
            viewModelScope.launch {
                try {
                    repository.insert(book.copy(id = 0))
                    lastDeletedBook = null
                } catch (e: Exception) {
                    uiState = BooksUiState.Error("No se pudo deshacer la eliminación")
                }
            }
        }
    }

    suspend fun getBookById(bookId: Int): BookEntity? {
        return repository.getById(bookId)
    }

    fun toggleFavoriteStatus(bookId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleFavoriteStatus(bookId, isFavorite)
                uiState = BooksUiState.Success("Estado de favorito actualizado")
            } catch (e: Exception) {
                uiState = BooksUiState.Error("Error al actualizar estado de favorito")
            }
        }
    }

    fun resetState() {
        uiState = BooksUiState.Idle
    }
}
