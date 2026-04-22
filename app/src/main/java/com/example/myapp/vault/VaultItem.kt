package com.example.myapp.vault

import java.util.UUID

data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    val app: String,
    val username: String,
    val password: String,
)
