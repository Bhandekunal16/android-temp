package com.example.myapp.components

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

fun checkEmailRegex(input: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(input).matches()

@Composable
fun HomeScreen(navController: NavController) {
    var text by remember { mutableStateOf("Hello Welcome!") }
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                if (input.isBlank() || !checkEmailRegex(input)) {
                    ToastService.toast(context, "please enter valid email")
                    NotificationService.showNotification(
                        context,
                        "please enter valid email",
                        "error",
                    )
                    return@Button
                }
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
