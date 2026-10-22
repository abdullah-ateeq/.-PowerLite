package com.example.powerlite.model

import java.math.RoundingMode

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class RoundingModeOption(val roundingMode: RoundingMode, val displayName: String) {
    HALF_UP(RoundingMode.HALF_UP, "Half Up"),
    HALF_DOWN(RoundingMode.HALF_DOWN, "Half Down"),
    HALF_EVEN(RoundingMode.HALF_EVEN, "Half Even"),
    UP(RoundingMode.UP, "Up"),
    DOWN(RoundingMode.DOWN, "Down"),
    CEILING(RoundingMode.CEILING, "Ceiling"),
    FLOOR(RoundingMode.FLOOR, "Floor")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmDelete: Boolean = true,
    val decimalPlaces: Int = 10,
    val roundingMode: RoundingModeOption = RoundingModeOption.HALF_UP
)
