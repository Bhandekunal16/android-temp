package com.example.myapp.components

import android.os.Build
import android.util.Patterns
import android.widget.Toast
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
import com.example.myapp.NotificationService
import com.example.myapp.R
import com.example.myapp.ToastService
import com.example.myapp.parseString
import com.example.myapp.storage.UserPrefs

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer, ignoreCase = true)) {
        model
    } else {
        "$manufacturer $model"
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val deviceName = remember { getDeviceName() }
    var text by remember { mutableStateOf("Hello Welcome!") }
    var input by remember { mutableStateOf(deviceName) }
    val context = LocalContext.current
    val savedUser = remember { UserPrefs.getUsername(context) }

    LaunchedEffect(Unit) {
        if (!savedUser.isNullOrBlank()) {
            navController.navigate("dashboard/$savedUser") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(stringResource(R.string.enterUsername)) },
        )

        Button(
            onClick = {
                if (input.isBlank()) {
                    ToastService.toast(context, "please enter valid user name")
                    NotificationService.showNotification(
                        context,
                        "please enter valid user name",
                        "error",
                    )
                    return@Button
                }
                UserPrefs.saveUsername(context, input)
                ToastService.toast(context, "Hello 👋")
                NotificationService.showNotification(context, "welcome $input", "Hello 👋")
                text = "Hello Welcome $input!"
                navController.navigate("dashboard/$input")
                input = ""
            },
            enabled = input.isNotBlank(),
        ) {
            Text(parseString(R.string.DashboardNav))
        }
    }
}
