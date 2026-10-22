package com.example.powerlite.domain

import java.math.BigDecimal
import java.math.BigInteger

class PowerLiteValidator(
    private val parser: PowerLiteParser = PowerLiteParser()
) {

    companion object {
        const val MAX_EXPONENT = 100_000
        const val MAX_ROOT_INDEX = 1_000
        const val MAX_SAFE_OUTPUT_DIGITS = 100_000
    }

    fun validatePowerInput(baseStr: String, expStr: String): Result<Pair<BigDecimal, BigInteger>> {
        val trimmedBase = baseStr.trim()
        val trimmedExp = expStr.trim()

        if (trimmedBase.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a base value."))
        }
        if (trimmedExp.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter an exponent."))
        }

        val baseResult = parser.parseDecimal(trimmedBase)
        if (baseResult.isFailure) {
            return Result.failure(baseResult.exceptionOrNull() ?: IllegalArgumentException("Enter a valid decimal number."))
        }

        val expResult = parser.parseInteger(trimmedExp)
        if (expResult.isFailure) {
            return Result.failure(expResult.exceptionOrNull() ?: IllegalArgumentException("Exponent must be a whole number."))
        }

        val base = baseResult.getOrThrow()
        val exp = expResult.getOrThrow()

        val absExp = exp.abs()
        if (absExp > BigInteger.valueOf(MAX_EXPONENT.toLong())) {
            return Result.failure(IllegalArgumentException("Exponent is too large. Maximum absolute exponent is 100,000."))
        }

        // Safety check for estimated output size:
        // For base != 0, 1, -1, check if result digits would exceed 100,000
        val baseStripped = base.stripTrailingZeros()
        val baseInt = baseStripped.toBigInteger().abs()
        if (baseInt > BigInteger.ONE && absExp > BigInteger.ZERO) {
            // Estimate digits: bitLength() / 3.32 ~ log10
            val bits = baseInt.bitLength().toLong()
            // approximate log10 as bits * 301 / 1000 + 1
            val approxLog10 = (bits * 301L / 1000L) + 1L
            val expLong = absExp.toLong() // since absExp <= 100,000, safe to convert to Long
            val estimatedDigits = approxLog10 * expLong
            if (estimatedDigits > MAX_SAFE_OUTPUT_DIGITS) {
                return Result.failure(IllegalArgumentException("Result is too large to display safely. Try a smaller exponent or base."))
            }
        }

        // Also check scale explosion for decimal base
        val scale = baseStripped.scale().toLong()
        if (scale > 0 && absExp > BigInteger.ZERO) {
            val estimatedScale = scale * absExp.toLong()
            if (estimatedScale > MAX_SAFE_OUTPUT_DIGITS) {
                return Result.failure(IllegalArgumentException("Result is too large to display safely. Try a smaller exponent or base."))
            }
        }

        return Result.success(Pair(base, exp))
    }

    fun validateRootInput(valueStr: String, indexStr: String): Result<Pair<BigDecimal, Int>> {
        val trimmedValue = valueStr.trim()
        val trimmedIndex = indexStr.trim()

        if (trimmedValue.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a value."))
        }
        if (trimmedIndex.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a root index."))
        }

        val valueResult = parser.parseDecimal(trimmedValue)
        if (valueResult.isFailure) {
            return Result.failure(valueResult.exceptionOrNull() ?: IllegalArgumentException("Enter a valid decimal number."))
        }

        val indexResult = parser.parseInteger(trimmedIndex)
        if (indexResult.isFailure) {
            return Result.failure(IllegalArgumentException("Root index must be a positive whole number."))
        }

        val value = valueResult.getOrThrow()
        val indexBig = indexResult.getOrThrow()

        if (indexBig <= BigInteger.ZERO) {
            return Result.failure(IllegalArgumentException("Root index must be a positive whole number."))
        }

        if (indexBig > BigInteger.valueOf(MAX_ROOT_INDEX.toLong())) {
            return Result.failure(IllegalArgumentException("Root index is too large. Maximum is 1000."))
        }

        return Result.success(Pair(value, indexBig.toInt()))
    }
}
