package com.example.practicafinallibros.ui.screen.books

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel

@Composable
fun BookListScreen(
    bookViewModel: BookViewModel,
    authViewModel: AuthViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val bookList = bookViewModel.bookList
    val showOnlyMine = bookViewModel.showOnlyMine
    val searchQuery = bookViewModel.searchQuery

    LaunchedEffect(authViewModel.userId) {
        authViewModel.userId?.let { bookViewModel.setCurrentUser(it) }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mis Libros", style = MaterialTheme.typography.headlineSmall)
            FloatingActionButton(
                onClick = onAddBook,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir libro")
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { bookViewModel.onSearchQueryChange(it) },
            label = { Text("Buscar libros...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = showOnlyMine,
                onClick = { bookViewModel.toggleShowOnlyMine() },
                label = { Text(if (showOnlyMine) "Solo míos" else "Todos") },
                leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filtro") }
            )
            Text("${bookList.size} libros", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(12.dp))

        if (bookList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay libros. ¡Añade el primero!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(bookList) { book ->
                    BookCard(
                        book = book,
                        showOwner = !showOnlyMine,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookCard(
    book: BookEntity,
    showOwner: Boolean,
    onClick: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.imageUri != null) {
                AsyncImage(
                    model = book.imageUri,
                    contentDescription = "Portada de ${book.title}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                if (showOwner) {
                    Text(
                        "Por: ${book.createdByName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}