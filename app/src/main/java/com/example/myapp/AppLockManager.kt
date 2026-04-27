package com.example.myapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppLockManager {
    var isLocked by mutableStateOf(true)
    var lastActiveTime = System.currentTimeMillis()
    private const val TIMEOUT = 30_000L 

    fun updateActivity() {
        lastActiveTime = System.currentTimeMillis()
    }

    fun checkLock() {
        val now = System.currentTimeMillis()
        if (now - lastActiveTime > TIMEOUT) {
            isLocked = true
        }
    }

    fun unlock() {
        isLocked = false
        updateActivity()
    }
}
