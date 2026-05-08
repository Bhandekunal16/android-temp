package com.example.myapp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastService {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun toast(
        context: Context,
        message: String,
    ) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } else {
            mainHandler.post {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
