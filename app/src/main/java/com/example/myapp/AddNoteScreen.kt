package com.example.myapp

import androidx.compose.foundation.layout.*
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
import com.example.myapp.vault.NotesManager
import com.example.myapp.vault.SecureNote

@Composable
fun AddNoteScreen(
    navController: NavController,
    noteId: String?,
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = NotesManager.getById(context, noteId)
            if (note != null) {
                title = note.title
                content = note.content
                isEditMode = true
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = if (isEditMode) "Edit Note" else "Add Note",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isBlank() || content.isBlank()) return@Button

                if (isEditMode && noteId != null) {
                    NotesManager.update(
                        context,
                        SecureNote(
                            id = noteId,
                            title = title,
                            content = content,
                        ),
                    )
                } else {
                    NotesManager.save(
                        context,
                        SecureNote(
                            title = title,
                            content = content,
                        ),
                    )
                }

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isEditMode) "Update" else "Save")
        }
    }
}
