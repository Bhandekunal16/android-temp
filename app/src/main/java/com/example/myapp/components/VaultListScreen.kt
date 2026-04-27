package com.example.myapp.components

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.myapp.AppLockManager
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
    val paddingSixteen = Modifier.padding(16.dp)
    var searchQuery by remember { mutableStateOf("") }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000) // check every 5 sec
            AppLockManager.checkLock()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        AppLockManager.updateActivity()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        AppLockManager.checkLock()
                    }

                    else -> {}
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (AppLockManager.isLocked) {
        LaunchedEffect(AppLockManager.isLocked) {
            if (AppLockManager.isLocked && activity != null) {
                showBiometricPrompt(
                    activity = activity,
                    onSuccess = {
                        AppLockManager.unlock()
                    },
                    onError = {},
                    onFailed = {},
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("🔒 Unlocking...")
        }

        return
    }
    LaunchedEffect(refreshTrigger) {
        try {
            items = VaultManager.getAllDecrypted(context)
        } catch (e: Exception) {
            e.printStackTrace()
            items = emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = { navController.navigate("add") }, modifier = paddingSixteen)
        { Text(text = stringResource(R.string.AddPassword)) }
        Text(text = title, modifier = paddingSixteen, color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Search by app or username") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                    ) { Icon(Icons.Default.Close, contentDescription = "Clear") }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.NoPasswordsSaved),
                modifier = paddingSixteen,
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            val passwordCounts = remember(items) { items.groupingBy { it.password }.eachCount() }
            val filteredItems =
                remember(items, searchQuery) {
                    if (searchQuery.isBlank()) {
                        items
                    } else {
                        items.filter {
                            it.app.contains(searchQuery, ignoreCase = true) ||
                                it.username.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }
            if (filteredItems.isEmpty()) {
                Text(
                    text = if (searchQuery.isBlank()) stringResource(R.string.NoPasswordsSaved) else "No results found 🔍",
                    modifier = paddingSixteen,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                LazyColumn {
                    items(filteredItems, key = { it.id }) { item ->
                        var showPassword by remember(item.id) { mutableStateOf(false) }
                        val strengthResult = remember(item.password) { evaluatePassword(item.password) }
                        val (label, color) =
                            when (strengthResult.strength) {
                                PasswordStrength.WEAK -> "Weak ❌" to Color.Red
                                PasswordStrength.MEDIUM -> "Medium ⚠️" to Color(0xFFFFA000)
                                PasswordStrength.STRONG -> "Strong ✅" to Color(0xFF2E7D32)
                            }
                        val reuseCount = passwordCounts[item.password] ?: 0
                        val isReused = reuseCount > 1

                        Card(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Column(modifier = paddingSixteen) {
                                Text(text = item.app, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = item.username, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = if (showPassword) item.password else "********", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small)
                                {
                                    Text(
                                        text = label,
                                        color = color,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }

                                if (isReused) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(color = Color.Red.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small)
                                    {
                                        Text(
                                            text = "Reused in $reuseCount apps ⚠️",
                                            color = Color.Red,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = (strengthResult.score + 1) / 5f,
                                    color = color,
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = { showPassword = !showPassword },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (showPassword) {
                                                    Icons.Default.VisibilityOff
                                                } else {
                                                    Icons.Default.Visibility
                                                },
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
                                    ) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
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
                                    ) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
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
                                                    val clipboard =
                                                        context.getSystemService(
                                                            android.content.Context.CLIPBOARD_SERVICE,
                                                        ) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("password", item.password))
                                                    ToastService.toast(context, "Copied to clipboard 📋")
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        val current = clipboard.primaryClip?.getItemAt(0)?.text
                                                        if (current ==
                                                            item.password
                                                        ) {
                                                            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                                                        }
                                                    }, 30_000)
                                                },
                                                onError = { ToastService.toast(context, "Auth error ❌") },
                                                onFailed = { ToastService.toast(context, "Auth failed ❌") },
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                    ) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
