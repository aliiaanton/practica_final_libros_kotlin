package com.example.practicafinallibros.ui.state

sealed class BooksUiState {

    data object Idle: BooksUiState()
    data object Loading: BooksUiState()
    data class Success(val message: String): BooksUiState()
    data class Error(val message: String): BooksUiState()

}