package com.example.powerlite.domain.math

import com.example.powerlite.model.CalculationStep
import com.example.powerlite.model.PowerResult
import com.example.powerlite.util.SuperscriptUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

class PowerCalculator {

    fun powInteger(
        base: BigDecimal,
        exponent: BigInteger,
        mathContext: MathContext
    ): BigDecimal {
        if (exponent == BigInteger.ZERO) {
            return BigDecimal.ONE
        }
        var res = BigDecimal.ONE
        var b = base
        var e = exponent
        while (e > BigInteger.ZERO) {
            if (e.testBit(0)) {
                res = res.multiply(b)
            }
            e = e.shiftRight(1)
            if (e > BigInteger.ZERO) {
                b = b.multiply(b)
            }
        }
        return res
    }

    fun calculatePower(
        base: BigDecimal,
        exponent: BigInteger,
        mathContext: MathContext = MathContext(80, java.math.RoundingMode.HALF_UP)
    ): Result<PowerResult> {
        val isBaseZero = base.compareTo(BigDecimal.ZERO) == 0
        val isExpZero = exponent == BigInteger.ZERO

        if (isBaseZero && isExpZero) {
            return Result.failure(ArithmeticException("0⁰ is undefined in this calculator."))
        }

        if (isBaseZero && exponent < BigInteger.ZERO) {
            return Result.failure(ArithmeticException("Cannot calculate: zero cannot be raised to a negative power."))
        }

        val baseDisplay = if (base < BigDecimal.ZERO) "($base)" else base.toPlainString()
        val expSuperscript = SuperscriptUtil.toSuperscript(exponent)

        if (isBaseZero) {
            val steps = listOf(
                CalculationStep(
                    label = "Zero Base Rule",
                    expression = "0 raised to any positive whole power",
                    result = "0"
                )
            )
            return Result.success(
                PowerResult(
                    base = base,
                    exponent = exponent,
                    result = BigDecimal.ZERO,
                    isExact = true,
                    calculationSteps = steps
                )
            )
        }

        if (isExpZero) {
            val steps = listOf(
                CalculationStep(
                    label = "Zero Exponent Rule",
                    expression = "Any non-zero number raised to the power 0 equals 1.",
                    result = "1"
                ),
                CalculationStep(
                    label = "Expression",
                    expression = "$baseDisplay⁰",
                    result = "1"
                )
            )
            return Result.success(
                PowerResult(
                    base = base,
                    exponent = exponent,
                    result = BigDecimal.ONE,
                    isExact = true,
                    calculationSteps = steps
                )
            )
        }

        if (exponent > BigInteger.ZERO) {
            val result = powInteger(base, exponent, mathContext)
            val steps = mutableListOf<CalculationStep>()
            steps.add(
                CalculationStep(
                    label = "Expression",
                    expression = "$baseDisplay$expSuperscript",
                    result = ""
                )
            )

            val expInt = if (exponent <= BigInteger.valueOf(5)) exponent.toInt() else -1
            if (expInt in 2..5) {
                val repeated = List(expInt) { baseDisplay }.joinToString(" × ")
                steps.add(
                    CalculationStep(
                        label = "Repeated Multiplication",
                        expression = "= $repeated",
                        result = ""
                    )
                )
                // Intermediate product steps
                var intermediate = base
                for (i in 2 until expInt) {
                    intermediate = intermediate.multiply(base)
                    val remaining = List(expInt - i) { baseDisplay }.joinToString(" × ")
                    val interDisplay = if (intermediate < BigDecimal.ZERO) "($intermediate)" else intermediate.toPlainString()
                    steps.add(
                        CalculationStep(
                            label = "Multiply",
                            expression = "= $interDisplay × $remaining",
                            result = ""
                        )
                    )
                }
            } else {
                steps.add(
                    CalculationStep(
                        label = "Method",
                        expression = "Exponentiation by squaring (O(log n))",
                        result = "Exact calculation"
                    )
                )
            }

            steps.add(
                CalculationStep(
                    label = "Result",
                    expression = "= ${result.stripTrailingZeros().toPlainString()}",
                    result = result.stripTrailingZeros().toPlainString()
                )
            )

            return Result.success(
                PowerResult(
                    base = base,
                    exponent = exponent,
                    result = result,
                    isExact = true,
                    calculationSteps = steps
                )
            )
        } else {
            // Negative exponent
            val absExp = exponent.abs()
            val absExpSuperscript = SuperscriptUtil.toSuperscript(absExp)
            val denominator = powInteger(base, absExp, mathContext)
            val result = BigDecimal.ONE.divide(denominator, mathContext)

            // Check if exact
            val isExact = result.multiply(denominator, mathContext).stripTrailingZeros()
                .compareTo(BigDecimal.ONE) == 0

            val steps = mutableListOf<CalculationStep>()
            steps.add(
                CalculationStep(
                    label = "Negative Exponent Definition",
                    expression = "$baseDisplay$expSuperscript",
                    result = "1 / ($baseDisplay$absExpSuperscript)"
                )
            )
            val denomDisplay = denominator.stripTrailingZeros().toPlainString()
            steps.add(
                CalculationStep(
                    label = "Calculate Denominator",
                    expression = "= 1 / $denomDisplay",
                    result = ""
                )
            )
            steps.add(
                CalculationStep(
                    label = "Division",
                    expression = "= ${result.stripTrailingZeros().toPlainString()}",
                    result = if (isExact) "Exact result" else "High-precision decimal approximation"
                )
            )

            return Result.success(
                PowerResult(
                    base = base,
                    exponent = exponent,
                    result = result,
                    isExact = isExact,
                    calculationSteps = steps
                )
            )
        }
    }
}
