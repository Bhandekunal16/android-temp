package com.example.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.routes.Routes
import com.example.myapp.utils.str
import com.example.myapp.vault.NotesManager
import com.example.myapp.vault.SecureNote

@Composable
fun NotesScreen(navController: NavController) {
    val context = LocalContext.current
    var notes by remember { mutableStateOf(emptyList<SecureNote>()) }
    var searchQuery by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    val padding = Modifier.padding(16.dp)

    LaunchedEffect(refreshTrigger) {
        notes = NotesManager.getAllDecrypted(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate(Routes.ADD_NOTE) },
            modifier = padding.align(Alignment.End),
        ) {
            Text(R.string.secure_notes.str())
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            placeholder = { Text(R.string.search_notes.str()) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )

        Spacer(modifier = Modifier.height(12.dp))

        val filteredNotes =
            remember(notes, searchQuery) {
                val source = notes
                if (searchQuery.isBlank()) {
                    source
                } else {
                    source.filter {
                        it.title.contains(searchQuery, true) ||
                            it.content.contains(searchQuery, true)
                    }
                }
            }

        if (filteredNotes.isEmpty()) {
            Text(
                text = R.string.no_notes.str(),
                modifier = padding,
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            LazyColumn {
                items(filteredNotes, key = { it.id }) { note: SecureNote ->

                    Card(
                        modifier =
                            Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    ) {
                        Column(modifier = padding) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = note.content.take(120) + if (note.content.length > 120) "..." else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate("add_note?noteId=${note.id}")
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }

                                Button(
                                    onClick = {
                                        NotesManager.delete(context, note.id)
                                        refreshTrigger++
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                        ),
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
