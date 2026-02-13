package com.example.practicafinallibros.ui.screen.admin

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.practicafinallibros.data.local.entity.UserEntity
import com.example.practicafinallibros.ui.state.AdminUiState
import com.example.practicafinallibros.ui.viewmodel.AdminViewModel
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.util.ConnectivityUtil

@Composable
fun AdminUserListScreen(
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel
) {
    val uiState = adminViewModel.uiState
    val token = authViewModel.token ?: ""
    val context = LocalContext.current
    val isOnline = ConnectivityUtil.isOnline(context)

    LaunchedEffect(Unit) {
        if (isOnline) {
            adminViewModel.refreshUsers(token)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState) {
        when (uiState) {
            is AdminUiState.Success -> {
                snackbarHostState.showSnackbar(uiState.message)
                adminViewModel.resetState()
            }
            is AdminUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
                adminViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gestión de usuarios", style = MaterialTheme.typography.headlineSmall)
                IconButton(
                    onClick = { adminViewModel.refreshUsers(token) },
                    enabled = isOnline
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                }
            }

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
                        Text("Sin conexión. Los datos pueden no estar actualizados.", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (uiState is AdminUiState.Loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(adminViewModel.userList) { user ->
                    AdminUserCard(
                        user = user,
                        isSelf = user.id == authViewModel.userId,
                        onUpdateName = { newName ->
                            adminViewModel.updateUserName(token, user.id, newName)
                        },
                        onDelete = {
                            adminViewModel.deleteUser(token, user.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: UserEntity,
    isSelf: Boolean,
    onUpdateName: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user.name) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            if (isEditing) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (editName.isNotBlank()) {
                            onUpdateName(editName.trim())
                            isEditing = false
                        }
                    }) { Text("Guardar") }
                    OutlinedButton(onClick = {
                        editName = user.name
                        isEditing = false
                    }) { Text("Cancelar") }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(user.name, style = MaterialTheme.typography.titleMedium)
                        Text(user.email, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Rol: ${user.role}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isSelf) {
                        Row {
                            IconButton(onClick = { isEditing = true }) {
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
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Eliminar a \"${user.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}