package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager

@Composable
fun VaultListScreen(navController: NavController) {
    val context = LocalContext.current

    var items by remember { mutableStateOf<List<VaultItem>>(emptyList()) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        try {
            items = VaultManager.getAllDecrypted(context)
        } catch (e: Exception) {
            e.printStackTrace()
            items = emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ➕ Add button
        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier.padding(16.dp),
        ) {
            Text("Add Password")
        }

        // 📋 Empty state
        if (items.isEmpty()) {
            Text(
                text = "No passwords saved",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            LazyColumn {
                items(items, key = { it.id }) { item ->

                    // ✅ Per-item state
                    var showPassword by remember { mutableStateOf(false) }

                    Card(
                        modifier =
                            Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 📱 App name
                            Text(
                                text = item.app,
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // 👤 Username
                            Text(
                                text = item.username,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // 🔐 Password
                            Text(
                                text = if (showPassword) item.password else "••••••••",
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 🎯 Actions
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // 👁 Show / Hide
                                OutlinedButton(
                                    onClick = { showPassword = !showPassword },
                                ) {
                                    Text(if (showPassword) "Hide" else "Show")
                                }

                                // ✏️ Edit
                                Button(
                                    onClick = {
                                        navController.navigate("add?itemId=${item.id}")
                                    },
                                ) {
                                    Text("Edit")
                                }

                                // 🗑 Delete
                                Button(
                                    onClick = {
                                        VaultManager.delete(context, item.id)
                                        refreshTrigger++ // 🔄 refresh list
                                    },
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                        ),
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
