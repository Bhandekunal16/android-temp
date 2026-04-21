package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.NotificationService
import com.example.myapp.R
import com.example.myapp.parseString

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
        Text(text = text, modifier = Modifier.padding(16.dp))

        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text(parseString(R.string.enterUsername)) },
        )

        Button(onClick = {
            if (input.isBlank()) {
                NotificationService.showNotification(
                    context,
                    "please enter valid username",
                    "error",
                )
                return@Button
            }
            NotificationService.showNotification(context, "welcome $input", "Hello 👋")
            text = "Hello Welcome $input!"
            navController.navigate("dashboard/$input")
            input = ""
        }) {
            Text(parseString(R.string.DashboardNav))
        }
    }
}
