package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.components.DashboardItemCard
import com.example.myapp.parseString
import com.example.myapp.routes.Routes
import com.example.myapp.utils.str

private fun Hight(input: Dp) = Modifier.height(input)

@Composable
fun DashboardScreen(
    username: String,
    navController: NavController,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
        ) {
            Text(
                text = R.string.Dashboard.str(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Hight(6.dp))

            Text(
                text = "Welcome back, \n $username 👋",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Hight(24.dp))

            DashboardItemCard(
                title = R.string.password_vault.str(),
                description = R.string.password_vault_desc.str(),
                onClick = { navController.navigate(Routes.VAULT) },
            )

            Spacer(modifier = Hight(16.dp))

            DashboardItemCard(
                title = R.string.add_password.str(),
                description = R.string.add_password_desc.str(),
                onClick = { navController.navigate(Routes.ADD) },
            )
        }
    }
}
