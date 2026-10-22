package com.example.powerlite.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.powerlite.model.AppSettings
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.RoundingModeOption
import com.example.powerlite.model.ThemeMode

class SettingsRepository(
    context: Context? = null,
    private val prefs: SharedPreferences = context!!.getSharedPreferences("powerlite_settings", Context.MODE_PRIVATE)
) {

    companion object {
        private const val KEY_THEME = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations_enabled"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"
        private const val KEY_DECIMAL_PLACES = "decimal_places"
        private const val KEY_ROUNDING_MODE = "rounding_mode"

        private const val KEY_LAST_MODE = "last_mode"
        private const val KEY_LAST_BASE = "last_base"
        private const val KEY_LAST_EXPONENT = "last_exponent"
        private const val KEY_LAST_ROOT_VALUE = "last_root_value"
        private const val KEY_LAST_ROOT_INDEX = "last_root_index"
    }

    fun getSettings(): AppSettings {
        val themeStr = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val theme = try { ThemeMode.valueOf(themeStr) } catch (_: Exception) { ThemeMode.SYSTEM }

        val dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val animations = prefs.getBoolean(KEY_ANIMATIONS, true)
        val confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        val decimalPlaces = prefs.getInt(KEY_DECIMAL_PLACES, 10)

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingModeOption.HALF_UP.name) ?: RoundingModeOption.HALF_UP.name
        val rounding = try { RoundingModeOption.valueOf(roundingStr) } catch (_: Exception) { RoundingModeOption.HALF_UP }

        return AppSettings(
            themeMode = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = haptic,
            animationsEnabled = animations,
            confirmDelete = confirmDelete,
            decimalPlaces = decimalPlaces,
            roundingMode = rounding
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.themeMode.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putBoolean(KEY_HAPTIC, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animationsEnabled)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putInt(KEY_DECIMAL_PLACES, settings.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            .apply()
    }

    fun resetSettings(): AppSettings {
        val defaultSettings = AppSettings()
        saveSettings(defaultSettings)
        return defaultSettings
    }

    fun getLastOperation(): OperationType {
        val modeStr = prefs.getString(KEY_LAST_MODE, OperationType.POWER.name) ?: OperationType.POWER.name
        return try { OperationType.valueOf(modeStr) } catch (_: Exception) { OperationType.POWER }
    }

    fun saveLastOperation(mode: OperationType) {
        prefs.edit().putString(KEY_LAST_MODE, mode.name).apply()
    }

    fun getLastPowerInputs(): Pair<String, String> {
        val base = prefs.getString(KEY_LAST_BASE, "") ?: ""
        val exp = prefs.getString(KEY_LAST_EXPONENT, "") ?: ""
        return Pair(base, exp)
    }

    fun saveLastPowerInputs(base: String, exponent: String) {
        prefs.edit()
            .putString(KEY_LAST_BASE, base)
            .putString(KEY_LAST_EXPONENT, exponent)
            .apply()
    }

    fun getLastRootInputs(): Pair<String, String> {
        val value = prefs.getString(KEY_LAST_ROOT_VALUE, "") ?: ""
        val index = prefs.getString(KEY_LAST_ROOT_INDEX, "2") ?: "2"
        return Pair(value, index)
    }

    fun saveLastRootInputs(value: String, index: String) {
        prefs.edit()
            .putString(KEY_LAST_ROOT_VALUE, value)
            .putString(KEY_LAST_ROOT_INDEX, index)
            .apply()
    }
}
