package com.example.myapp.vault

import android.content.Context
import com.example.myapp.security.CryptoManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object VaultManager {
    private const val FILE_NAME = "vault.json"
    private val gson = Gson()

    private fun getFile(context: Context): File = File(context.filesDir, FILE_NAME)

    // ✅ GET ALL (RAW - encrypted)
    fun getAll(context: Context): List<VaultItem> {
        return try {
            val file = getFile(context)

            if (!file.exists()) return emptyList()

            val json = file.readText()

            if (json.isBlank()) return emptyList()

            val type = object : TypeToken<List<VaultItem>>() {}.type
            gson.fromJson<List<VaultItem>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // 🔥 NEVER crash
        }
    }

    fun getAllDecrypted(context: Context): List<VaultItem> =
        try {
            getAll(context).mapNotNull { item ->
                try {
                    item.copy(
                        password = CryptoManager.decrypt(item.password),
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null // ❌ skip broken item
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    // ✅ SAVE
    fun save(
        context: Context,
        item: VaultItem,
    ) {
        val list = getAll(context).toMutableList()

        val encryptedItem =
            item.copy(
                password = CryptoManager.encrypt(item.password),
            )

        list.add(encryptedItem)

        val json = gson.toJson(list)
        getFile(context).writeText(json)
    }

    // ✅ UPDATE
    fun update(
        context: Context,
        updatedItem: VaultItem,
    ) {
        val list = getAll(context).toMutableList()

        val index = list.indexOfFirst { it.id == updatedItem.id }

        if (index != -1) {
            val encryptedItem =
                updatedItem.copy(
                    password = CryptoManager.encrypt(updatedItem.password),
                )
            list[index] = encryptedItem
        }

        val json = gson.toJson(list)
        getFile(context).writeText(json)
    }

    // ✅ DELETE
    fun delete(
        context: Context,
        itemId: String,
    ) {
        val list = getAll(context).toMutableList()

        val updatedList = list.filter { it.id != itemId }

        val json = gson.toJson(updatedList)
        getFile(context).writeText(json)
    }
}
