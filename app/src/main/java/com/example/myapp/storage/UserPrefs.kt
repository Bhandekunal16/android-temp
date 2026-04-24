package com.example.myapp.storage

import android.content.Context

object UserPrefs {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"

    fun saveUsername(
        context: Context,
        username: String,
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USERNAME, null)
    }
}
