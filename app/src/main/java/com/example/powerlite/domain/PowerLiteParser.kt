package com.example.powerlite.domain

import java.math.BigDecimal
import java.math.BigInteger

class PowerLiteParser {

    companion object {
        const val MAX_INPUT_LENGTH = 500
    }

    fun parseDecimal(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 characters."))
        }
        if (trimmed.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException("Internal spaces are not allowed."))
        }
        if (trimmed.contains('e') || trimmed.contains('E')) {
            return Result.failure(IllegalArgumentException("Enter a standard decimal number."))
        }

        val sanitized = if (trimmed.startsWith("+")) trimmed.substring(1) else trimmed
        if (sanitized.isEmpty() || sanitized == "-" || sanitized == ".") {
            return Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }

        // Validate characters: optional '-' followed by digits and at most one '.'
        val hasValidChars = sanitized.matches(Regex("^-?\\d+(\\.\\d+)?$")) ||
                sanitized.matches(Regex("^-?\\.\\d+$")) // e.g. .5
        if (!hasValidChars) {
            return Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }

        return try {
            Result.success(BigDecimal(sanitized))
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }
    }

    fun parseInteger(input: String): Result<BigInteger> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a valid whole number."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 characters."))
        }
        if (trimmed.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException("Internal spaces are not allowed."))
        }
        if (trimmed.contains('e') || trimmed.contains('E')) {
            return Result.failure(IllegalArgumentException("Enter a standard decimal number."))
        }
        if (trimmed.contains('.')) {
            return Result.failure(IllegalArgumentException("Exponent must be a whole number."))
        }

        val sanitized = if (trimmed.startsWith("+")) trimmed.substring(1) else trimmed
        if (sanitized.isEmpty() || sanitized == "-") {
            return Result.failure(IllegalArgumentException("Enter a valid whole number."))
        }

        val hasValidChars = sanitized.matches(Regex("^-?\\d+$"))
        if (!hasValidChars) {
            return Result.failure(IllegalArgumentException("Exponent must be a whole number."))
        }

        return try {
            Result.success(BigInteger(sanitized))
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException("Exponent must be a whole number."))
        }
    }
}
