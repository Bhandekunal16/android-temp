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
        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier.padding(16.dp),
        ) {
            Text("Add Password")
        }
        if (items.isEmpty()) {
            Text(
                text = "No passwords saved",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            LazyColumn {
                items(items, key = { it.id }) { item ->

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
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.app,
                                style = MaterialTheme.typography.titleMedium,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.username,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (showPassword) item.password else "••••••••",
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { showPassword = !showPassword },
                                ) {
                                    Text(if (showPassword) "Hide" else "Show")
                                }
                                Button(
                                    onClick = {
                                        navController.navigate("add?itemId=${item.id}")
                                    },
                                ) {
                                    Text("Edit")
                                }
                                Button(
                                    onClick = {
                                        VaultManager.delete(context, item.id)
                                        refreshTrigger++
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
