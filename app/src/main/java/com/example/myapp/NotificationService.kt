package com.example.myapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationService {
    val post_notification = Manifest.permission.POST_NOTIFICATIONS
    val permission_granted = PackageManager.PERMISSION_GRANTED

    fun checkSdkVersion(): Boolean = Build.VERSION.SDK_INT >= 33

    fun requestNotificationPermission(activity: ComponentActivity) {
        if (checkSdkVersion()) {
            if (ContextCompat.checkSelfPermission(activity, post_notification) != permission_granted) {
                ActivityCompat.requestPermissions(activity, arrayOf(post_notification), 1)
            }
        }
    }

    fun hasNotificationPermission(context: Context): Boolean =
        if (checkSdkVersion()) {
            ContextCompat.checkSelfPermission(context, post_notification) == permission_granted
        } else {
            true
        }

    @SuppressLint("MissingPermission")
    fun showNotification(
        context: Context,
        message: String,
        title: String,
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }

        val channelId = "my_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .build()

        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
