package com.example.practicafinallibros.ui.screen.books

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.notifications.NotificationHelper
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookDetailScreen(
    bookViewModel: BookViewModel,
    authViewModel: AuthViewModel,
    bookId: Int,
    context: Context,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit
) {
    var book by remember { mutableStateOf<BookEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        book = bookViewModel.getBookById(bookId)
    }

    if (book == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentBook = book!!
    val isOwner = currentBook.createdBy == authViewModel.userId

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            if (isOwner) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (currentBook.imageUri != null) {
            AsyncImage(
                model = currentBook.imageUri,
                contentDescription = "Portada de ${currentBook.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
        }

        Text(currentBook.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("por ${currentBook.author}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Text(
            "Creado por: ${currentBook.createdByName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        Text(
            "Fecha: ${dateFormat.format(Date(currentBook.createdAt))}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        currentBook.genre?.let {
            Text("Género: $it", style = MaterialTheme.typography.bodyMedium)
        }
        currentBook.pageCount?.let {
            Text("Páginas: $it", style = MaterialTheme.typography.bodyMedium)
        }
        currentBook.publishYear?.let {
            Text("Año publicación: $it", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(16.dp))

        Text("Descripción", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Text(currentBook.description, style = MaterialTheme.typography.bodyMedium)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar libro") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${currentBook.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    bookViewModel.deleteBook(currentBook)
                    NotificationHelper.sendSimpleNotification(
                        context,
                        "Libro eliminado",
                        "Se ha eliminado \"${currentBook.title}\""
                    )
                    showDeleteDialog = false
                    onDeleted()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}