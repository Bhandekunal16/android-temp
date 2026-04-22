package com.example.myapp

import android.content.Context
import android.widget.Toast

object ToastService {
    fun toast(
        context: Context,
        message: String,
    ) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
