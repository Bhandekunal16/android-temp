package com.example.myapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.routes.Routes
import com.example.myapp.utils.str

@Composable
fun DashboardScreen(
    username: String,
    navController: NavController,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = R.string.Dashboard.str(),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = R.string.welcome_back.str(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(12.dp))
            DashboardItemCard(
                title = R.string.password_vault.str(),
                description = R.string.password_vault_desc.str(),
                onClick = { navController.navigate(Routes.VAULT) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            DashboardItemCard(
                title = R.string.add_password.str(),
                description = R.string.add_password_desc.str(),
                onClick = { navController.navigate(Routes.ADD) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            DashboardItemCard(
                title = R.string.secure_notes.str(),
                description = R.string.secure_notes_desc.str(),
                onClick = { navController.navigate(Routes.NOTES) },
            )
        }
    }
}
