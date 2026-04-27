package com.example.myapp

import android.app.Activity
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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
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
    val ObservatoryDark =
        darkColorScheme(
            primary = Color(0xFF00D0A8),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF06695A),
            onPrimaryContainer = Color(0xFF8BFFDF),
            secondary = Color(0xFF00A88B),
            onSecondary = Color.White,
            background = Color(0xFF00352F),
            onBackground = Color(0xFFEEFFFA),
            surface = Color(0xFF0A574B),
            onSurface = Color(0xFFEEFFFA),
            surfaceVariant = Color(0xFF06695A),
            onSurfaceVariant = Color(0xFFC5FFEE),
            outline = Color(0xFF008E77),
            error = Color(0xFFFF6B6B),
            onError = Color.White,
        )

    val ObservatoryLight =
        lightColorScheme(
            primary = Color(0xFF008E77),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF8BFFDF),
            onPrimaryContainer = Color(0xFF00352F),
            secondary = Color(0xFF00A88B),
            onSecondary = Color.White,
            background = Color(0xFFEEFFFA),
            onBackground = Color(0xFF00352F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF00352F),
            surfaceVariant = Color(0xFFC5FFEE),
            onSurfaceVariant = Color(0xFF0A574B),
            outline = Color(0xFF00A88B),
            error = Color(0xFFD32F2F),
            onError = Color.White,
        )

    val colors = if (darkTheme) ObservatoryDark else ObservatoryLight

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colors.primary.toArgb()
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
