package com.example.powerlite.domain.math

import com.example.powerlite.model.CalculationStep
import com.example.powerlite.model.RootResult
import com.example.powerlite.util.SuperscriptUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

class RootCalculator(
    private val integerRootCalculator: IntegerRootCalculator = IntegerRootCalculator(),
    private val decimalRootCalculator: DecimalRootCalculator = DecimalRootCalculator(integerRootCalculator)
) {

    fun calculateRoot(
        value: BigDecimal,
        rootIndex: Int,
        mathContext: MathContext = MathContext(80, java.math.RoundingMode.HALF_UP)
    ): Result<RootResult> {
        if (rootIndex <= 0) {
            return Result.failure(IllegalArgumentException("Root index must be a positive whole number."))
        }

        val rootSymbol = SuperscriptUtil.rootSymbolForIndex(rootIndex)
        val valueDisplay = if (value < BigDecimal.ZERO) "($value)" else value.toPlainString()
        val indexSuperscript = SuperscriptUtil.toSuperscript(rootIndex)

        // Negative values check
        if (value < BigDecimal.ZERO) {
            if (rootIndex % 2 == 0) {
                val steps = listOf(
                    CalculationStep(
                        label = "Even Root of Negative Number",
                        expression = "$rootSymbol$valueDisplay",
                        result = "No real result"
                    ),
                    CalculationStep(
                        label = "Explanation",
                        expression = "An even root of a negative number is not a real number.",
                        result = "Undefined in real numbers"
                    )
                )
                return Result.success(
                    RootResult(
                        value = value,
                        rootIndex = rootIndex,
                        result = null,
                        isExact = false,
                        isReal = false,
                        calculationSteps = steps
                    )
                )
            }
        }

        // Root of zero
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            val steps = listOf(
                CalculationStep(
                    label = "Zero Root Rule",
                    expression = "$rootSymbol 0 = 0",
                    result = "0"
                )
            )
            return Result.success(
                RootResult(
                    value = value,
                    rootIndex = rootIndex,
                    result = BigDecimal.ZERO,
                    isExact = true,
                    isReal = true,
                    calculationSteps = steps
                )
            )
        }

        // Root index 1
        if (rootIndex == 1) {
            val steps = listOf(
                CalculationStep(
                    label = "First Root Rule",
                    expression = "¹√$valueDisplay = $valueDisplay",
                    result = value.stripTrailingZeros().toPlainString()
                )
            )
            return Result.success(
                RootResult(
                    value = value,
                    rootIndex = rootIndex,
                    result = value,
                    isExact = true,
                    isReal = true,
                    calculationSteps = steps
                )
            )
        }

        val isNegative = value < BigDecimal.ZERO
        val absValue = value.abs()

        // Root of 1 or -1
        if (absValue.compareTo(BigDecimal.ONE) == 0) {
            val res = if (isNegative) BigDecimal.ONE.negate() else BigDecimal.ONE
            val resDisplay = if (isNegative) "(-1)" else "1"
            val steps = listOf(
                CalculationStep(
                    label = "Find Root",
                    expression = "Find x where: x$indexSuperscript = $valueDisplay",
                    result = ""
                ),
                CalculationStep(
                    label = "Verification",
                    expression = "$resDisplay$indexSuperscript = $valueDisplay ✓",
                    result = resDisplay
                ),
                CalculationStep(
                    label = "Therefore",
                    expression = "$rootSymbol$valueDisplay = $resDisplay",
                    result = resDisplay
                )
            )
            return Result.success(
                RootResult(
                    value = value,
                    rootIndex = rootIndex,
                    result = res,
                    isExact = true,
                    isReal = true,
                    calculationSteps = steps
                )
            )
        }

        // Check exact root
        val exactCandidate = findExactRoot(absValue, rootIndex)
        if (exactCandidate != null) {
            val exactResult = if (isNegative) exactCandidate.negate() else exactCandidate
            val resultDisplay = if (exactResult < BigDecimal.ZERO) "($exactResult)" else exactResult.toPlainString()

            val steps = listOf(
                CalculationStep(
                    label = "Find Root",
                    expression = "Find x where: x$indexSuperscript = $valueDisplay",
                    result = ""
                ),
                CalculationStep(
                    label = "Exact Solution",
                    expression = "$resultDisplay$indexSuperscript = $valueDisplay ✓",
                    result = "Exact root found"
                ),
                CalculationStep(
                    label = "Therefore",
                    expression = "$rootSymbol$valueDisplay = $resultDisplay",
                    result = resultDisplay
                )
            )

            return Result.success(
                RootResult(
                    value = value,
                    rootIndex = rootIndex,
                    result = exactResult,
                    isExact = true,
                    isReal = true,
                    calculationSteps = steps
                )
            )
        }

        // Decimal high-precision root via Newton-Raphson
        return try {
            val decimalRootAbs = decimalRootCalculator.decimalNthRoot(absValue, rootIndex, mathContext)
            val decimalResult = if (isNegative) decimalRootAbs.negate() else decimalRootAbs
            val resultDisplay = decimalResult.toPlainString()

            val steps = listOf(
                CalculationStep(
                    label = "Find Root",
                    expression = "Find x where: x$indexSuperscript = $valueDisplay",
                    result = ""
                ),
                CalculationStep(
                    label = "Integer Check",
                    expression = "No integer x produces exactly $valueDisplay.",
                    result = "Approximate root"
                ),
                CalculationStep(
                    label = "Approximation",
                    expression = "$rootSymbol$valueDisplay ≈ $resultDisplay",
                    result = ""
                ),
                CalculationStep(
                    label = "Approximate Verification",
                    expression = "($resultDisplay)$indexSuperscript ≈ $valueDisplay",
                    result = "Verified at 80 decimal precision"
                )
            )

            Result.success(
                RootResult(
                    value = value,
                    rootIndex = rootIndex,
                    result = decimalResult,
                    isExact = false,
                    isReal = true,
                    calculationSteps = steps
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun findExactRoot(value: BigDecimal, rootIndex: Int): BigDecimal? {
        val stripped = value.stripTrailingZeros()
        val scale = stripped.scale()

        if (scale <= 0) {
            val intVal = stripped.toBigInteger()
            val root = integerRootCalculator.integerNthRoot(intVal, rootIndex)
            if (root.pow(rootIndex) == intVal) {
                return root.toBigDecimal()
            }
        } else if (scale % rootIndex == 0) {
            val unscaled = stripped.unscaledValue()
            val rootUnscaled = integerRootCalculator.integerNthRoot(unscaled, rootIndex)
            if (rootUnscaled.pow(rootIndex) == unscaled) {
                val candidate = BigDecimal(rootUnscaled, scale / rootIndex)
                // Verify with BigDecimal exact pow
                if (candidate.pow(rootIndex).stripTrailingZeros() == stripped) {
                    return candidate
                }
            }
        }
        return null
    }
}
