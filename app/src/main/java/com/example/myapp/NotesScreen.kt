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

@Composable
fun NotesScreen(navController: NavController) {
    val context = LocalContext.current
    val notes = remember { NotesManager.getAll(context) }

    Column {
        notes.forEach { note ->
            Text(note.title)
        }

        Button(onClick = { navController.navigate(Routes.ADD_NOTE) }) {
            Text("Add Note")
        }
    }
}
