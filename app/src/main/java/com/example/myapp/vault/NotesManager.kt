package com.example.myapp.vault

import android.content.Context
import com.example.myapp.security.CryptoManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object NotesManager {
    private const val FILE_NAME = "note.json"
    private val gson = Gson()

    private fun getFile(context: Context): File = File(context.filesDir, FILE_NAME)

    fun getById(
        context: Context,
        id: String,
    ): SecureNote? = getAllDecrypted(context).find { it.id == id }

    private fun writeJsonFile(
        input: List<SecureNote>,
        context: Context,
    ) {
        val json = gson.toJson(input)
        getFile(context).writeText(json)
    }

    fun getAll(context: Context): List<SecureNote> {
        return try {
            val file = getFile(context)

            if (!file.exists()) return emptyList()

            val json = file.readText()

            if (json.isBlank()) return emptyList()

            val type = object : TypeToken<List<SecureNote>>() {}.type
            gson.fromJson<List<SecureNote>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getAllDecrypted(context: Context): List<SecureNote> =
        try {
            getAll(context).mapNotNull { item ->
                try {
                    item.copy(content = CryptoManager.decrypt(item.content))
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

    fun save(
        context: Context,
        item: SecureNote,
    ) {
        val list = getAll(context).toMutableList()
        val encryptedItem = item.copy(content = CryptoManager.encrypt(item.content))
        list.add(encryptedItem)
        writeJsonFile(list, context)
    }

    fun update(
        context: Context,
        updatedItem: SecureNote,
    ) {
        val list = getAll(context).toMutableList()
        val index = list.indexOfFirst { it.id == updatedItem.id }

        if (index != -1) {
            val encryptedItem = updatedItem.copy(content = CryptoManager.encrypt(updatedItem.content))
            list[index] = encryptedItem
        }

        writeJsonFile(list, context)
    }

    fun delete(
        context: Context,
        itemId: String,
    ) {
        val list = getAll(context).toMutableList()
        val updatedList = list.filter { it.id != itemId }
        writeJsonFile(updatedList, context)
    }
}
