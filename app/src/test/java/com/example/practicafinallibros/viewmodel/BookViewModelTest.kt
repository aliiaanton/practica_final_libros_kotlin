package com.example.practicafinallibros.viewmodel

import com.example.practicafinallibros.MainDispatcherRule
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.repository.BookRepository
import com.example.practicafinallibros.ui.state.BooksUiState
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class BookViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BookViewModel
    private val repository: BookRepository = mock()

    private val testBooks = listOf(
        BookEntity(id = 1, title = "Libro 1", author = "Autor 1", description = "", imageUri = null, createdAt = 0L, createdBy = "user1", createdByName = "User 1", genre = null, pageCount = null, publishYear = null),
        BookEntity(id = 2, title = "Libro 2", author = "Autor 2", description = "", imageUri = null, createdAt = 0L, createdBy = "user2", createdByName = "User 2", genre = null, pageCount = null, publishYear = null)
    )

    @Before
    fun setup() {
        whenever(repository.observeAll()).doReturn(flowOf(testBooks))
        whenever(repository.observeFavorites()).doReturn(flowOf(emptyList()))
        whenever(repository.observeByUser(any())).doReturn(flowOf(emptyList()))
        whenever(repository.observeSearch(any())).doReturn(flowOf(emptyList()))
        
        viewModel = BookViewModel(repository)
    }

    @Test
    fun `loadBooks sets bookList from repository`() = runTest {
        viewModel.setCurrentUser("user1")
        assertEquals(testBooks, viewModel.bookList)
    }

    @Test
    fun `addBook calls repository insert and updates state to success`() = runTest {
        val newBook = testBooks[0]
        viewModel.addBook(newBook)
        
        verify(repository).insert(newBook)
        assertTrue(viewModel.uiState is BooksUiState.Success)
    }

    @Test
    fun `deleteBook calls repository delete and sends event`() = runTest {
        val bookToDelete = testBooks[0]
        viewModel.deleteBook(bookToDelete)
        
        verify(repository).delete(bookToDelete)
    }

    @Test
    fun `toggleShowOnlyMine reloads books from user specific flow`() = runTest {
        val user1Books = listOf(testBooks[0])
        whenever(repository.observeByUser("user1")).doReturn(flowOf(user1Books))
        
        viewModel.setCurrentUser("user1")
        viewModel.toggleShowOnlyMine() 
        
        assertEquals(user1Books, viewModel.bookList)
    }

    @Test
    fun `addBook sets error state when repository fails`() = runTest {
        whenever(repository.insert(any())).thenThrow(RuntimeException("Error de BD"))
        
        viewModel.addBook(testBooks[0])
        
        assertTrue(viewModel.uiState is BooksUiState.Error)
        assertEquals("Error al añadir el libro", (viewModel.uiState as BooksUiState.Error).message)
    }
}
