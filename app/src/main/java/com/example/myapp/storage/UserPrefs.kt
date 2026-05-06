package com.example.myapp.storage

import android.content.Context

object UserPrefs {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_ID = "id"

    private fun prefs(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUsername(
        context: Context,
        username: String,
    ) {
        prefs(context).edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(context: Context): String? = prefs(context).getString(KEY_USERNAME, null)

    fun saveId(
        context: Context,
        id: String,
    ) {
        prefs(context).edit().putString(KEY_ID, id).apply()
    }

    fun getId(context: Context): String? = prefs(context).getString(KEY_ID, null)
}
