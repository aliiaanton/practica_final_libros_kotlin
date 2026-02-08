package com.example.practicafinallibros.ui.screen.books

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.practicafinallibros.data.local.entity.BookEntity
import com.example.practicafinallibros.notifications.NotificationHelper
import com.example.practicafinallibros.ui.state.BooksUiState
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import java.io.File

@Composable
fun BookFormScreen(
    bookViewModel: BookViewModel,
    authViewModel: AuthViewModel,
    context: Context,
    bookId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isEditing = bookId != null

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var publishYear by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var originalCreatedAt by remember { mutableStateOf(System.currentTimeMillis()) }

    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    // Camera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    // Photo file URI for full-resolution camera capture
    val photoFileUri = remember {
        val file = File(context.cacheDir, "book_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoFileUri.toString()
        }
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // Persist URI access across reboots
            context.contentResolver.takePersistableUriPermission(
                uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = uri.toString()
        }
    }

    // Load existing book if editing
    LaunchedEffect(bookId) {
        if (isEditing && !loaded) {
            val book = bookViewModel.getBookById(bookId!!)
            if (book != null) {
                title = book.title
                author = book.author
                description = book.description
                genre = book.genre ?: ""
                pageCount = book.pageCount?.toString() ?: ""
                publishYear = book.publishYear?.toString() ?: ""
                imageUri = book.imageUri
                originalCreatedAt = book.createdAt
            }
            loaded = true
        }
    }

    // Navigate back on success
    val uiState = bookViewModel.uiState
    LaunchedEffect(uiState) {
        if (uiState is BooksUiState.Success) {
            bookViewModel.resetState()
            onSaved()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                if (isEditing) "Editar libro" else "Nuevo libro",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Image preview
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Portada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }

        // Camera & Gallery buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    if (hasCameraPermission) {
                        takePictureLauncher.launch(photoFileUri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Cámara")
            }
            OutlinedButton(
                onClick = { galleryLauncher.launch(arrayOf("image/*")) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Galería")
            }
        }

        // Form fields
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; titleError = false },
            label = { Text("Título *") },
            isError = titleError,
            supportingText = { if (titleError) Text("El título es obligatorio") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it; authorError = false },
            label = { Text("Autor *") },
            isError = authorError,
            supportingText = { if (authorError) Text("El autor es obligatorio") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it; descriptionError = false },
            label = { Text("Descripción *") },
            isError = descriptionError,
            supportingText = { if (descriptionError) Text("La descripción es obligatoria") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Género") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = pageCount,
            onValueChange = { pageCount = it },
            label = { Text("Número de páginas") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = publishYear,
            onValueChange = { publishYear = it },
            label = { Text("Año de publicación") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        if (uiState is BooksUiState.Error) {
            Text(
                (uiState as BooksUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                titleError = title.isBlank()
                authorError = author.isBlank()
                descriptionError = description.isBlank()
                if (titleError || authorError || descriptionError) return@Button

                val book = BookEntity(
                    id = bookId ?: 0,
                    title = title.trim(),
                    author = author.trim(),
                    description = description.trim(),
                    imageUri = imageUri,
                    createdAt = if (isEditing) originalCreatedAt else System.currentTimeMillis(),
                    createdBy = authViewModel.userId ?: "",
                    createdByName = authViewModel.userName ?: "",
                    genre = genre.trim().ifBlank { null },
                    pageCount = pageCount.toIntOrNull(),
                    publishYear = publishYear.toIntOrNull()
                )

                if (isEditing) {
                    bookViewModel.updateBook(book)
                    NotificationHelper.sendSimpleNotification(
                        context, "Libro actualizado", "Se ha actualizado \"${title.trim()}\""
                    )
                } else {
                    bookViewModel.addBook(book)
                    NotificationHelper.sendSimpleNotification(
                        context, "Libro creado", "Se ha creado \"${title.trim()}\""
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is BooksUiState.Loading
        ) {
            if (uiState is BooksUiState.Loading) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(if (isEditing) "Guardar cambios" else "Crear libro")
            }
        }
    }
}