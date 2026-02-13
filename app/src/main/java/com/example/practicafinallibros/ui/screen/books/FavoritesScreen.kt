package com.example.practicafinallibros.ui.screen.books

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicafinallibros.R
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.ui.components.SkeletonBookList
import com.example.practicafinallibros.ui.viewmodel.BookViewModel

@Composable
fun FavoritesScreen(
    bookViewModel: BookViewModel,
    onBookClick: (Int) -> Unit
) {
    val favoriteBooks = bookViewModel.favoriteBooks
    val isLoadingFavorites = bookViewModel.isLoadingFavorites

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.favorites_title), style = MaterialTheme.typography.headlineSmall)
        
        Spacer(Modifier.height(12.dp))

        if (isLoadingFavorites && favoriteBooks.isEmpty()) {
            SkeletonBookList()
        } else if (favoriteBooks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_favorites))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favoriteBooks) { book ->
                    FavoriteBookCard(
                        book = book,
                        onClick = { onBookClick(book.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteBookCard(
    book: BookEntity,
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
                Text(
                    stringResource(R.string.by_owner, book.createdByName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
