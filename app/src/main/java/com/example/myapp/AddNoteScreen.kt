package com.example.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.components.findActivity
import com.example.myapp.routes.Routes
import com.example.myapp.security.CryptoManager
import com.example.myapp.utils.str
import com.example.myapp.vault.NotesManager
import com.example.myapp.vault.SecureNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddNoteScreen(
    navController: NavController,
    noteId: String?,
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

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

    fun persistAndSync(
        note: SecureNote,
        update: Boolean,
    ) {
        if (isSaving) return
        scope.launch {
            isSaving = true
            try {
                if (update) {
                    NotesManager.update(context, note)
                } else {
                    NotesManager.save(context, note)
                }

                val payload = Note(id = note.id, title = note.title, content = CryptoManager.encrypt(note.content))

                val response =
                    withContext(Dispatchers.IO) {
                        if (update) {
                            RetrofitClient.api.updateNote(payload)
                        } else {
                            RetrofitClient.api.saveNote(payload)
                        }
                    }

                val msg =
                    if (response.isSuccessful) {
                        if (update) R.string.note_updated else R.string.note_added
                    } else {
                        R.string.sync_failed
                    }
                ToastService.toast(context, context.getString(msg))
            } catch (e: Exception) {
                e.printStackTrace()
                ToastService.toast(context, context.getString(R.string.sync_failed))
            } finally {
                isSaving = false
                navController.navigate(Routes.NOTES)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isEditMode) R.string.edit_note.str() else R.string.add_note.str(),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(R.string.title_label.str()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(R.string.content_label.str()) },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            enabled = !isSaving,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isBlank() || content.isBlank()) return@Button
                if (activity == null) return@Button

                if (!isBiometricAvailable(context)) {
                    ToastService.toast(context, context.getString(R.string.biometric_not_available))
                    return@Button
                }

                if (isEditMode && noteId != null) {
                    showBiometricPrompt(
                        activity = activity,
                        onSuccess = { persistAndSync(SecureNote(id = noteId, title = title, content = content), update = true) },
                        onError = {
                            ToastService.toast(context, context.getString(R.string.auth_error))
                            navController.popBackStack()
                        },
                        onFailed = {
                            ToastService.toast(context, context.getString(R.string.auth_failed))
                            navController.popBackStack()
                        },
                    )
                } else {
                    persistAndSync(SecureNote(title = title, content = content), update = false)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        ) {
            Text(if (isEditMode) R.string.update.str() else R.string.save.str())
        }
    }
}
