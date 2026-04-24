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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.vault.VaultItem
import com.example.myapp.vault.VaultManager

@Composable
fun AddPasswordScreen(
    navController: NavController,
    itemId: String?,
) {
    val context = LocalContext.current
    val items = remember { VaultManager.getAllDecrypted(context) }

    val existingItem = items.find { it.id == itemId }

    var app by remember { mutableStateOf(existingItem?.app ?: "") }
    var username by remember { mutableStateOf(existingItem?.username ?: "") }
    var password by remember { mutableStateOf(existingItem?.password ?: "") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (itemId == null) "Add Credential" else "Edit Credential",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = app, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onValueChange = {
            app = it
        }, label = { Text("App") })
        TextField(value = username, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onValueChange = {
            username = it
        }, label = { Text("Username") })
        TextField(value = password, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), onValueChange = {
            password = it
        }, label = { Text("Password") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (itemId == null) {
                    // ➕ ADD
                    VaultManager.save(
                        context,
                        VaultItem(app = app, username = username, password = password),
                    )
                } else {
                    // ✏️ UPDATE
                    VaultManager.update(
                        context,
                        VaultItem(
                            id = itemId,
                            app = app,
                            username = username,
                            password = password,
                        ),
                    )
                }

                navController.popBackStack()
            },
        ) {
            Text(if (itemId == null) "Save" else "Update")
        }
    }
}
