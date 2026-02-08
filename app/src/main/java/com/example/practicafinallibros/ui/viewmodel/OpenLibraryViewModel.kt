package com.example.practicafinallibros.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicafinallibros.data.remote.dto.OpenLibraryBookDto
import com.example.practicafinallibros.data.repository.OpenLibraryRepository
import com.example.practicafinallibros.ui.state.BooksUiState
import kotlinx.coroutines.launch

class OpenLibraryViewModel(
    private val repository: OpenLibraryRepository
): ViewModel() {

    var uiState by mutableStateOf<BooksUiState>(BooksUiState.Idle)
        private set

    var bookList by mutableStateOf<List<OpenLibraryBookDto>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun searchBooks() {
        if (searchQuery.isBlank()) return
        viewModelScope.launch {
            uiState = BooksUiState.Loading
            try {
                bookList = repository.searchBooks(searchQuery)
                uiState = BooksUiState.Idle
            } catch (e: Exception) {
                uiState = BooksUiState.Error("No se pudo cargar la información. Revisa tu conexión.")
            }
        }
    }
}
