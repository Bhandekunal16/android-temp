package com.example.myapp.components

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.myapp.R
import com.example.myapp.ToastService
import com.example.myapp.isBiometricAvailable
import com.example.myapp.routes.Routes
import com.example.myapp.showBiometricPrompt
import com.example.myapp.utils.str

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.padding(24.dp).fillMaxWidth().heightIn(min = 220.dp, max = 280.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = R.string.password_manager.str(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = R.string.authentication_required.str(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (activity == null) {
                                ToastService.toast(context, context.getString(R.string.activity_error))
                                return@Button
                            }

                            if (!isBiometricAvailable(context)) {
                                ToastService.toast(context, context.getString(R.string.biometric_not_available))
                                return@Button
                            }

                            isLoading = true

                            showBiometricPrompt(
                                activity = activity,
                                onSuccess = {
                                    ToastService.toast(context, context.getString(R.string.authenticated))
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.AUTH) { inclusive = true }
                                    }
                                },
                                onError = {
                                    isLoading = false
                                    ToastService.toast(context, context.getString(R.string.error))
                                },
                                onFailed = {
                                    isLoading = false
                                    ToastService.toast(context, context.getString(R.string.failed))
                                },
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(R.string.unlock.str())
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
