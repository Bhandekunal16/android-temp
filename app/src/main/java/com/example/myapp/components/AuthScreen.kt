package com.example.myapp.components

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.myapp.ToastService
import com.example.myapp.isBiometricAvailable
import com.example.myapp.showBiometricPrompt

fun Context.findActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

@Composable
fun AuthScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Authentication Required")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (activity == null) {
                    ToastService.toast(context, "Activity error ❌")
                    return@Button
                }

                if (!isBiometricAvailable(context)) {
                    ToastService.toast(context, "Biometric not available ❌")
                    return@Button
                }

                isLoading = true

                showBiometricPrompt(
                    activity = activity,
                    onSuccess = {
                        ToastService.toast(context, "Authenticated ✅")

                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onError = {
                        isLoading = false
                        ToastService.toast(context, "Error ❌")
                    },
                    onFailed = {
                        isLoading = false
                        ToastService.toast(context, "Failed ❌")
                    },
                )
            },
        ) {
            Text("Unlock")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
