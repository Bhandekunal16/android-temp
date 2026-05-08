package com.example.myapp.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.Password
import com.example.myapp.R
import com.example.myapp.RetrofitClient
import com.example.myapp.ToastService
import com.example.myapp.storage.UserPrefs
import com.example.myapp.utils.str
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager
import kotlinx.coroutines.*

private fun textAddon() = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)

private fun ContainerAddon() = Modifier.padding(16.dp)

fun Password.toVaultItem(): VaultItem =
    VaultItem(
        id = this.id,
        app = this.app,
        username = this.username,
        password = this.password,
    )

@Composable
fun AddPasswordScreen(
    navController: NavController,
    itemId: String? = null,
    app: String = "",
    username: String = "",
    password: String = "",
) {
    val context = LocalContext.current

    var appText by remember { mutableStateOf(app) }
    var usernameText by remember { mutableStateOf(username) }
    var passwordText by remember { mutableStateOf(password) }

    Column(
        modifier = ContainerAddon(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (itemId == null) R.string.add_credential.str() else R.string.edit_credential.str(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = ContainerAddon())

        TextField(value = appText, modifier = textAddon(), onValueChange = { appText = it }, label = { Text(R.string.app_label.str()) })
        TextField(
            value = usernameText,
            modifier = textAddon(),
            onValueChange = { usernameText = it },
            label = { Text(R.string.app_username.str()) },
        )
        TextField(
            value = passwordText,
            modifier = textAddon(),
            onValueChange = { passwordText = it },
            label = { Text(R.string.app_password.str()) },
        )

        Spacer(modifier = ContainerAddon())

        Button(
            onClick = {
                if (appText.isBlank() || usernameText.isBlank() || passwordText.isBlank()) {
                    ToastService.toast(context, context.getString(R.string.required_fields_missing))
                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val id = UserPrefs.getId(context)

                        val finalId =
                            id ?: itemId ?: java.util.UUID
                                .randomUUID()
                                .toString()
                        val request =
                            Password(
                                id = finalId,
                                app = appText,
                                username = usernameText,
                                password = passwordText,
                            )

                        val response =
                            if (itemId == null) {
                                RetrofitClient.api.savePassword(request)
                            } else {
                                RetrofitClient.api.updatePassword(request)
                            }

                        if (response.body()?.status == true) {
                            withContext(Dispatchers.Main) {
                                if (itemId == null) {
                                    VaultManager.save(context, request.toVaultItem())
                                } else {
                                    VaultManager.update(context, request.toVaultItem())
                                }
                                navController.popBackStack()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                ToastService.toast(context, "Failed to save password")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Exception: ${e.message}", e)

                        withContext(Dispatchers.Main) {
                            ToastService.toast(context, "Error: ${e.message}")
                        }
                    }
                }
            },
        ) {
            Text(if (itemId == null) R.string.save.str() else R.string.update.str())
        }
    }
}
