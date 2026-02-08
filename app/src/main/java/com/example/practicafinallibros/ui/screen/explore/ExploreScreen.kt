package com.example.practicafinallibros.ui.screen.explore

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.data.remote.dto.OpenLibraryBookDto
import com.example.practicafinallibros.notifications.NotificationHelper
import com.example.practicafinallibros.ui.state.BooksUiState
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import com.example.practicafinallibros.ui.viewmodel.OpenLibraryViewModel
import com.example.practicafinallibros.util.ConnectivityUtil

@Composable
fun ExploreScreen(
    openLibraryViewModel: OpenLibraryViewModel,
    bookViewModel: BookViewModel,
    authViewModel: AuthViewModel,
    context: Context
) {
    val uiState = openLibraryViewModel.uiState
    val isOnline = ConnectivityUtil.isOnline(context)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Explorar libros", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (!isOnline) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Sin conexión a internet. Conéctate para buscar libros.", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = openLibraryViewModel.searchQuery,
                onValueChange = { openLibraryViewModel.onSearchQueryChange(it) },
                label = { Text("Buscar en Open Library") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (!ConnectivityUtil.isOnline(context)) {
                        // UI state change would require VM, so just skip
                        return@IconButton
                    }
                    openLibraryViewModel.searchBooks()
                }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
        }

        Spacer(Modifier.height(12.dp))

        when (uiState) {
            is BooksUiState.Loading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BooksUiState.Error -> {
                Text(
                    uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {}
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(openLibraryViewModel.bookList) { book ->
                OpenLibraryBookCard(
                    book = book,
                    onAdd = {
                        val entity = BookEntity(
                            title = book.title,
                            author = book.authorName?.joinToString(", ") ?: "Desconocido",
                            description = "Importado desde Open Library",
                            imageUri = book.coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
                            createdAt = System.currentTimeMillis(),
                            createdBy = authViewModel.userId ?: "",
                            createdByName = authViewModel.userName ?: "",
                            genre = book.subject?.firstOrNull(),
                            pageCount = book.numberOfPagesMedian,
                            publishYear = book.firstPublishYear
                        )
                        bookViewModel.addBook(entity)
                        NotificationHelper.sendSimpleNotification(
                            context,
                            "Libro añadido",
                            "\"${book.title}\" añadido a tu colección"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun OpenLibraryBookCard(
    book: OpenLibraryBookDto,
    onAdd: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (book.coverId != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.coverId}-M.jpg",
                    contentDescription = "Portada de ${book.title}",
                    modifier = Modifier
                        .width(60.dp)
                        .height(90.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            }

            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                book.authorName?.joinToString(", ")?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                book.firstPublishYear?.let {
                    Text("Año: $it", style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Añadir a mi colección")
            }
        }
    }
}