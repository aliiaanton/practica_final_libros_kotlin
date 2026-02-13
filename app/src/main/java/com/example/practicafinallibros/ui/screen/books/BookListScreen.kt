package com.example.practicafinallibros.ui.screen.books

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicafinallibros.R
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.ui.state.BooksUiState
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.practicafinallibros.ui.components.SkeletonBookList


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
    val uiState = bookViewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authViewModel.userId) {
        authViewModel.userId?.let { bookViewModel.setCurrentUser(it) }
    }

    LaunchedEffect(Unit) {
        bookViewModel.eventFlow.collectLatest { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                bookViewModel.undoDelete()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_book))
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(stringResource(R.string.nav_my_books), style = MaterialTheme.typography.headlineSmall)
            
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { bookViewModel.onSearchQueryChange(it) },
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
                    label = { Text(if (showOnlyMine) stringResource(R.string.filter_mine) else stringResource(R.string.filter_all)) },
                    leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) }
                )
                Text(stringResource(R.string.books_count, bookList.size), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(12.dp))

            if (uiState is BooksUiState.Loading && bookList.isEmpty()) {
                SkeletonBookList()
            } else if (bookList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_books))
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
                    contentDescription = null,
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
                        stringResource(R.string.by_owner, book.createdByName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
