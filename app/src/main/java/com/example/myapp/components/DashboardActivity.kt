package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.parseString

@Composable
fun DashboardScreen(
    username: String,
    navController: NavController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = parseString(R.string.Dashboard),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(parseString(R.string.dashboardGreeting, username))

        Spacer(modifier = Modifier.height(24.dp))

        // 🔐 Go to Vault List
        Button(
            onClick = {
                navController.navigate("vault")
            },
        ) {
            Text("Open Vault")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ➕ Direct Add Entry
        Button(
            onClick = {
                navController.navigate("add")
            },
        ) {
            Text("Add Password")
        }
    }
}
