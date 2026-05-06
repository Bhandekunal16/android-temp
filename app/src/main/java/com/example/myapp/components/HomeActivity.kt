package com.example.myapp.components

import android.os.Build
import android.util.Log
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
import com.example.myapp.AuthRequest
import com.example.myapp.NotificationService
import com.example.myapp.R
import com.example.myapp.RetrofitClient
import com.example.myapp.ToastService
import com.example.myapp.parseString
import com.example.myapp.routes.Routes
import com.example.myapp.storage.UserPrefs
import com.example.myapp.utils.str
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer, ignoreCase = true)) model else "$manufacturer $model"
}

@Composable
fun HomeScreen(navController: NavController) {
    val deviceName = remember { getDeviceName() }
    var text = R.string.welcome.str()
    var input by remember { mutableStateOf(deviceName) }
    val context = LocalContext.current
    val savedUser = remember { UserPrefs.getUsername(context) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!savedUser.isNullOrBlank()) {
            navController.navigate("dashboard/$savedUser") { popUpTo(Routes.HOME) { inclusive = true } }
        }
    }

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
                if (input.isBlank()) {
                    ToastService.toast(context, context.getString(R.string.valid_username))
                    NotificationService.showNotification(context, context.getString(R.string.valid_username), context.getString(R.string.e))
                    return@Button
                }

                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val response = RetrofitClient.api.getAuth(AuthRequest(input))
                    Log.d("API_DEBUG", "Response: $response")
                    val username = input
                    if (response.code() == 200) {
                        withContext(Dispatchers.Main) {
                            Log.d("API_DEBUG", "Response: ${response.body()?.auth?._id}")
                            val userId = response.body()?.auth?._id ?: ""
                            Log.d("API_DEBUG", "Response: $userId")
                            UserPrefs.saveUsername(context, input)
                            UserPrefs.saveId(context, userId)
                            ToastService.toast(context, context.getString(R.string.hello))
                            NotificationService.showNotification(context, "welcome $input", context.getString(R.string.hello))
                            text = "Hello Welcome $input!"
                            navController.navigate("dashboard/$username")
                            input = ""
                        }
                    } else {
                        val saveRes = RetrofitClient.api.saveAuth(AuthRequest(input))
                        if (saveRes.code() == 200) {
                            withContext(Dispatchers.Main) {
                                val response1 = RetrofitClient.api.getAuth(AuthRequest(input))
                                if (response1.code() == 200) {
                                    withContext(Dispatchers.Main) {
                                        val userId = response1.body()?.auth?._id ?: ""
                                        Log.d("API_DEBUG", "Response: $userId")
                                        UserPrefs.saveUsername(context, input)
                                        UserPrefs.saveId(context, userId)
                                        navController.navigate("dashboard/$username")
                                        input = ""
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                NotificationService.showNotification(context, "something went's wrong", context.getString(R.string.e))
                            }
                        }
                    }
                }
            },
            enabled = input.isNotBlank(),
        ) {
            Text(parseString(R.string.DashboardNav))
        }
    }
}
