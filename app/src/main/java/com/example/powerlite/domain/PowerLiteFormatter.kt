package com.example.powerlite.domain

import com.example.powerlite.model.PowerResult
import com.example.powerlite.model.RootResult
import com.example.powerlite.util.SuperscriptUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class PowerLiteFormatter {

    fun formatResult(
        value: BigDecimal,
        isExact: Boolean,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        if (isExact) {
            val stripped = value.stripTrailingZeros()
            return stripped.toPlainString()
        } else {
            val scaled = value.setScale(decimalPlaces, roundingMode).stripTrailingZeros()
            return if (scaled.compareTo(BigDecimal.ZERO) == 0) "0" else scaled.toPlainString()
        }
    }

    fun formatPowerExpression(base: BigDecimal, exponent: BigInteger): String {
        val baseStr = if (base < BigDecimal.ZERO) "($base)" else base.toPlainString()
        val expSuperscript = SuperscriptUtil.toSuperscript(exponent)
        return "$baseStr$expSuperscript"
    }

    fun formatRootExpression(value: BigDecimal, rootIndex: Int): String {
        val rootSymbol = SuperscriptUtil.rootSymbolForIndex(rootIndex)
        val valueStr = if (value < BigDecimal.ZERO) "($value)" else value.toPlainString()
        return "$rootSymbol$valueStr"
    }

    fun formatCopyResult(
        powerResult: PowerResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val base = powerResult.base
        val exp = powerResult.exponent
        val baseDisplay = if (base < BigDecimal.ZERO) "($base)" else base.toPlainString()
        val formattedResult = formatResult(powerResult.result, powerResult.isExact, decimalPlaces, roundingMode)
        val symbol = if (powerResult.isExact) "=" else "≈"
        val status = if (powerResult.isExact) "Exact Result" else "Approximate Result"

        return buildString {
            appendLine("PowerLite")
            appendLine()
            appendLine("$baseDisplay^$exp $symbol $formattedResult")
            appendLine()
            append(status)
        }
    }

    fun formatCopySummary(
        powerResult: PowerResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val base = powerResult.base
        val exp = powerResult.exponent
        val baseDisplay = if (base < BigDecimal.ZERO) "($base)" else base.toPlainString()
        val formattedResult = formatResult(powerResult.result, powerResult.isExact, decimalPlaces, roundingMode)
        val symbol = if (powerResult.isExact) "=" else "≈"
        val status = if (powerResult.isExact) "Exact" else "Approximate"

        return buildString {
            appendLine("PowerLite")
            appendLine()
            appendLine("Operation: Power")
            appendLine()
            appendLine("Base: $base")
            appendLine("Exponent: $exp")
            appendLine()
            appendLine("$baseDisplay^$exp $symbol $formattedResult")
            appendLine()
            appendLine("Result: $formattedResult")
            append("Status: $status")
        }
    }

    fun formatCopyResult(
        rootResult: RootResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val expr = formatRootExpression(rootResult.value, rootResult.rootIndex)
        if (!rootResult.isReal || rootResult.result == null) {
            return buildString {
                appendLine("PowerLite")
                appendLine()
                appendLine("$expr = No real result")
                appendLine()
                append("No Real Result")
            }
        }

        val formattedResult = formatResult(rootResult.result, rootResult.isExact, decimalPlaces, roundingMode)
        val symbol = if (rootResult.isExact) "=" else "≈"
        val status = if (rootResult.isExact) "Exact Result" else "Approximate Result"

        return buildString {
            appendLine("PowerLite")
            appendLine()
            appendLine("$expr $symbol $formattedResult")
            appendLine()
            append(status)
        }
    }

    fun formatCopySummary(
        rootResult: RootResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val expr = formatRootExpression(rootResult.value, rootResult.rootIndex)
        val status = when {
            !rootResult.isReal || rootResult.result == null -> "No real result"
            rootResult.isExact -> "Exact"
            else -> "Approximate"
        }
        val formattedResult = if (rootResult.result != null) {
            formatResult(rootResult.result, rootResult.isExact, decimalPlaces, roundingMode)
        } else {
            "No real result"
        }
        val symbol = if (rootResult.isExact) "=" else "≈"

        return buildString {
            appendLine("PowerLite")
            appendLine()
            appendLine("Operation: Root")
            appendLine()
            appendLine("Value: ${rootResult.value}")
            appendLine("Root Index: ${rootResult.rootIndex}")
            appendLine()
            appendLine("$expr $symbol $formattedResult")
            appendLine()
            appendLine("Result: $formattedResult")
            append("Status: $status")
        }
    }

    fun formatShareText(
        powerResult: PowerResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val expr = formatPowerExpression(powerResult.base, powerResult.exponent)
        val formattedResult = formatResult(powerResult.result, powerResult.isExact, decimalPlaces, roundingMode)
        val symbol = if (powerResult.isExact) "=" else "≈"
        val status = if (powerResult.isExact) "Exact Result" else "Approximate Result"

        return buildString {
            appendLine("PowerLite Calculation")
            appendLine()
            appendLine("$expr $symbol $formattedResult")
            appendLine("Status: $status")
            appendLine()
            append("Calculated with PowerLite")
        }
    }

    fun formatShareText(
        rootResult: RootResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val expr = formatRootExpression(rootResult.value, rootResult.rootIndex)
        if (!rootResult.isReal || rootResult.result == null) {
            return buildString {
                appendLine("PowerLite Calculation")
                appendLine()
                appendLine("$expr = No real result")
                appendLine("Status: No real result")
                appendLine()
                append("Calculated with PowerLite")
            }
        }

        val formattedResult = formatResult(rootResult.result, rootResult.isExact, decimalPlaces, roundingMode)
        val symbol = if (rootResult.isExact) "=" else "≈"
        val status = if (rootResult.isExact) "Exact Result" else "Approximate Result"

        return buildString {
            appendLine("PowerLite Calculation")
            appendLine()
            appendLine("$expr $symbol $formattedResult")
            appendLine("Status: $status")
            appendLine()
            append("Calculated with PowerLite")
        }
    }
}
