package com.example.myapp.components

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
import com.example.myapp.R
import com.example.myapp.ToastService
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager

private fun textAddon() = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)

private fun ContainerAddon() = Modifier.padding(16.dp)

@Composable
fun Int.str() = stringResource(this)

@Composable
fun AddPasswordScreen(
    navController: NavController,
    itemId: String?,
) {
    val context = LocalContext.current
    val items = remember { VaultManager.getAllDecrypted(context) }

    val existingItem = remember(itemId, items) { items.find { it.id == itemId } }

    var app by remember { mutableStateOf(existingItem?.app ?: "") }
    var username by remember { mutableStateOf(existingItem?.username ?: "") }
    var password by remember { mutableStateOf(existingItem?.password ?: "") }

    Column(
        modifier = ContainerAddon(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (itemId == null) R.string.AddCredential.str() else R.string.EditCredential.str(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = ContainerAddon())

        TextField(value = app, modifier = textAddon(), onValueChange = { app = it }, label = { Text(R.string.app_label.str()) })
        TextField(
            value = username,
            modifier = textAddon(),
            onValueChange = { username = it },
            label = { Text(R.string.app_username.str()) },
        )
        TextField(
            value = password,
            modifier = textAddon(),
            onValueChange = { password = it },
            label = { Text(R.string.app_password.str()) },
        )

        Spacer(modifier = ContainerAddon())

        Button(
            onClick = {
                if (app.isBlank() || username.isBlank() || password.isBlank()) {
                    ToastService.toast(context, context.getString(R.string.ToolTip))
                    return@Button
                } else if (itemId == null) {
                    VaultManager.save(context, VaultItem(app = app, username = username, password = password))
                } else {
                    VaultManager.update(context, VaultItem(id = itemId, app = app, username = username, password = password))
                }
                navController.popBackStack()
            },
        ) {
            Text(if (itemId == null) R.string.Save.str() else R.string.Update.str())
        }
    }
}
