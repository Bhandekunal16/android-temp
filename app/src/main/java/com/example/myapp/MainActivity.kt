package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapp.components.AddPasswordScreen
import com.example.myapp.components.AuthScreen
import com.example.myapp.components.DashboardScreen
import com.example.myapp.components.HomeScreen
import com.example.myapp.components.VaultListScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationService.requestNotificationPermission(this)
        setContent {
            MyAppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        NavHost(
            navController = navController,
            startDestination = "auth",
        ) {
            composable("auth") {
                AuthScreen(navController)
            }

            composable("home") {
                HomeScreen(navController)
            }

            composable("vault") {
                VaultListScreen(navController)
            }
            composable(
                route = "add?itemId={itemId}",
                arguments =
                    listOf(
                        navArgument("itemId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                    ),
            ) { backStackEntry ->

                val itemId = backStackEntry.arguments?.getString("itemId")

                AddPasswordScreen(navController, itemId)
            }

            composable("dashboard/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                DashboardScreen(username, navController)
            }
        }
    }
}

@Composable
fun parseString(
    id: Int,
    vararg args: Any,
): String = stringResource(id = id, *args)

@Preview
@Composable
fun PreviewApp() {
    MyAppTheme {
        MyApp()
    }
}

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF4DB6AC),
                onPrimary = Color(0xFF00201A),
                secondary = Color(0xFF9575CD),
                onSecondary = Color(0xFF1A1333),
                tertiary = Color(0xFFFFB74D),
                onTertiary = Color(0xFF2E1B00),
                background = Color(0xFF0F1115),
                onBackground = Color(0xFFE6EDF3),
                surface = Color(0xFF161B22),
                onSurface = Color(0xFFE6EDF3),
                surfaceVariant = Color(0xFF21262D),
                outline = Color(0xFF8B949E),
                error = Color(0xFFFF6B6B),
                onError = Color.White,
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF5E35B1),
                onPrimary = Color.White,
                secondary = Color(0xFF00838F),
                onSecondary = Color.White,
                tertiary = Color(0xFFFFA726),
                onTertiary = Color.Black,
                background = Color(0xFFF3E5F5),
                onBackground = Color(0xFF1A1A1A),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF1A1A1A),
                surfaceVariant = Color(0xFFE1BEE7),
                outline = Color(0xFF7B1FA2),
            )
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
