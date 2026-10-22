package com.example.powerlite.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.PowerLiteHistory
import java.util.UUID

class HistoryRepository(
    context: Context? = null,
    private val prefs: SharedPreferences = context!!.getSharedPreferences("powerlite_history", Context.MODE_PRIVATE)
) {

    companion object {
        private const val KEY_HISTORY = "history_entries"
        const val MAX_HISTORY_ITEMS = 10
        private const val ITEM_SEPARATOR = "\n"
        private const val FIELD_SEPARATOR = "\u001F" // Unit separator character
    }

    fun getHistory(): List<PowerLiteHistory> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        if (raw.isBlank()) return emptyList()

        val list = mutableListOf<PowerLiteHistory>()
        val lines = raw.split(ITEM_SEPARATOR)
        for (line in lines) {
            if (line.isBlank()) continue
            val parts = line.split(FIELD_SEPARATOR)
            if (parts.size >= 5) {
                val id = parts[0]
                val op = try { OperationType.valueOf(parts[1]) } catch (_: Exception) { OperationType.POWER }
                val firstInput = parts[2]
                val secondInput = parts[3]
                val savedAt = parts[4].toLongOrNull() ?: 0L
                list.add(
                    PowerLiteHistory(
                        id = id,
                        operation = op,
                        firstInput = firstInput,
                        secondInput = secondInput,
                        savedAt = savedAt
                    )
                )
            }
        }
        return list
    }

    fun addHistory(
        operation: OperationType,
        firstInput: String,
        secondInput: String
    ): List<PowerLiteHistory> {
        val current = getHistory().toMutableList()

        // Consecutive duplicate check
        if (current.isNotEmpty()) {
            val latest = current.first()
            if (latest.operation == operation &&
                latest.firstInput == firstInput &&
                latest.secondInput == secondInput
            ) {
                return current
            }
        }

        val newItem = PowerLiteHistory(
            id = UUID.randomUUID().toString(),
            operation = operation,
            firstInput = firstInput,
            secondInput = secondInput,
            savedAt = System.currentTimeMillis()
        )

        // Add to front
        current.add(0, newItem)

        // Limit to 10 entries
        val trimmed = if (current.size > MAX_HISTORY_ITEMS) {
            current.subList(0, MAX_HISTORY_ITEMS)
        } else {
            current
        }

        saveList(trimmed)
        return trimmed
    }

    fun deleteHistory(id: String): List<PowerLiteHistory> {
        val current = getHistory().filter { it.id != id }
        saveList(current)
        return current
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveList(list: List<PowerLiteHistory>) {
        val raw = list.joinToString(ITEM_SEPARATOR) { item ->
            listOf(
                item.id,
                item.operation.name,
                item.firstInput,
                item.secondInput,
                item.savedAt.toString()
            ).joinToString(FIELD_SEPARATOR)
        }
        prefs.edit().putString(KEY_HISTORY, raw).apply()
    }
}
