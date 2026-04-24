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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.ToastService
import com.example.myapp.components.findActivity
import com.example.myapp.isBiometricAvailable
import com.example.myapp.parseString
import com.example.myapp.showBiometricPrompt
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager

@Composable
fun VaultListScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context.findActivity()

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
            Text(text = stringResource(R.string.AddPassword))
        }
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.NoPasswordsSaved),
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
                                    Text(text = if (showPassword) stringResource(R.string.Hide) else stringResource(R.string.Show))
                                }
                                Button(
                                    onClick = {
                                        navController.navigate("add?itemId=${item.id}")
                                    },
                                ) {
                                    Text(text = stringResource(R.string.Edit))
                                }
                                Button(
                                    onClick = {
                                        if (activity == null) return@Button

                                        if (!isBiometricAvailable(context)) {
                                            ToastService.toast(context, "Biometric not available ❌")
                                            return@Button
                                        }

                                        showBiometricPrompt(
                                            activity = activity,
                                            onSuccess = {
                                                VaultManager.delete(context, item.id)
                                                refreshTrigger++
                                                ToastService.toast(context, "Deleted ✅")
                                            },
                                            onError = {
                                                ToastService.toast(context, "Auth error ❌")
                                            },
                                            onFailed = {
                                                ToastService.toast(context, "Auth failed ❌")
                                            },
                                        )
                                    },
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                        ),
                                ) {
                                    Text(text = stringResource(R.string.Delete))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
