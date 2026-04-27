package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.myapp.vault.PasswordStrength
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager
import com.example.myapp.vault.evaluatePassword

@Composable
fun VaultListScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val title by remember { mutableStateOf("Create, save and manage your passwords so that you can easily sign in to sites and apps.") }
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
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.NoPasswordsSaved),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            LazyColumn {
                items(items, key = { it.id }) { item ->
                    var showPassword by remember { mutableStateOf(false) }
                    val strengthResult = remember(item.password) { evaluatePassword(item.password) }
                    val (label, color) =
                        when (strengthResult.strength) {
                            PasswordStrength.WEAK -> "Weak ❌" to Color.Red
                            PasswordStrength.MEDIUM -> "Medium ⚠️" to Color(0xFFFFA000)
                            PasswordStrength.STRONG -> "Strong ✅" to Color(0xFF2E7D32)
                        }

                    Card(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = item.app, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = item.username, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = if (showPassword) item.password else "********", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = color.copy(alpha = 0.15f),
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Text(
                                    text = label,
                                    color = color,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = (strengthResult.score + 1) / 5f,
                                color = color,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(4.dp),
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedButton(onClick = { showPassword = !showPassword }, modifier = Modifier.weight(1f))
                                {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Password",
                                    )
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
                                            onSuccess = { navController.navigate("add?itemId=${item.id}") },
                                            onError = { ToastService.toast(context, "Auth error ❌") },
                                            onFailed = { ToastService.toast(context, "Auth failed ❌") },
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                ) { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit") }
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
                                            onError = { ToastService.toast(context, "Auth error ❌") },
                                            onFailed = { ToastService.toast(context, "Auth failed ❌") },
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                ) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete") }
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
                                                val shareText =
                                                    """
                                                    App: ${item.app}
                                                    Username: ${item.username}
                                                    Password: ${item.password}
                                                    """.trimIndent()

                                                val sendIntent =
                                                    android.content.Intent().apply {
                                                        action = android.content.Intent.ACTION_SEND
                                                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                                        type = "text/plain"
                                                    }

                                                val shareIntent = android.content.Intent.createChooser(sendIntent, "Share credentials")
                                                context.startActivity(shareIntent)
                                            },
                                            onError = { ToastService.toast(context, "Auth error ❌") },
                                            onFailed = { ToastService.toast(context, "Auth failed ❌") },
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                ) { Icon(imageVector = Icons.Default.Share, contentDescription = "Share") }
                            }
                        }
                    }
                }
            }
        }
    }
}
