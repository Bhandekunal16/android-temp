package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
        setContent { MyApp() }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

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
                primary = Color(0xFFBB86FC),
                background = Color.Black,
                onBackground = Color.White,
                surface = Color(0xFF121212),
                onSurface = Color.White,
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF6200EE),
                background = Color.White,
                onBackground = Color.Black,
                surface = Color.White,
                onSurface = Color.Black,
            )
        }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
