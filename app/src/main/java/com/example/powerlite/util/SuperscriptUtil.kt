package com.example.powerlite.util

import java.math.BigInteger

object SuperscriptUtil {

    private val superscriptMap = mapOf(
        '0' to '⁰',
        '1' to '¹',
        '2' to '²',
        '3' to '³',
        '4' to '⁴',
        '5' to '⁵',
        '6' to '⁶',
        '7' to '⁷',
        '8' to '⁸',
        '9' to '⁹',
        '-' to '⁻',
        '+' to '⁺'
    )

    fun toSuperscript(value: String): String {
        return value.map { superscriptMap[it] ?: it }.joinToString("")
    }

    fun toSuperscript(value: BigInteger): String {
        return toSuperscript(value.toString())
    }

    fun toSuperscript(value: Int): String {
        return toSuperscript(value.toString())
    }

    fun rootSymbolForIndex(rootIndex: Int): String {
        return when (rootIndex) {
            1 -> "¹√"
            2 -> "√"
            3 -> "∛"
            else -> "${toSuperscript(rootIndex)}√"
        }
    }

    fun rootAccessibilityDescription(rootIndex: Int, valueStr: String): String {
        val rootWord = when (rootIndex) {
            1 -> "First root"
            2 -> "Square root"
            3 -> "Cube root"
            4 -> "Fourth root"
            5 -> "Fifth root"
            6 -> "Sixth root"
            7 -> "Seventh root"
            8 -> "Eighth root"
            9 -> "Ninth root"
            10 -> "Tenth root"
            else -> "${rootIndex}th root"
        }
        return "$rootWord of $valueStr."
    }
}
