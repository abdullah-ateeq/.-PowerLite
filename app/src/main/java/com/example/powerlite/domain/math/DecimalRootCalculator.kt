package com.example.powerlite.domain.math

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.math.max

class DecimalRootCalculator(
    private val integerRootCalculator: IntegerRootCalculator = IntegerRootCalculator()
) {

    fun decimalNthRoot(
        value: BigDecimal,
        index: Int,
        mathContext: MathContext
    ): BigDecimal {
        require(value >= BigDecimal.ZERO) { "Value must be non-negative for decimal nth root." }
        require(index >= 1) { "Root index must be at least 1." }

        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
        if (value.compareTo(BigDecimal.ONE) == 0 || index == 1) return value

        // Determine initial guess without Double conversion
        var x = if (value > BigDecimal.ONE) {
            val intPart = value.toBigInteger()
            val intRoot = integerRootCalculator.integerNthRoot(intPart, index)
            if (intRoot > BigInteger.ZERO) intRoot.toBigDecimal() else BigDecimal.ONE
        } else {
            val stripped = value.stripTrailingZeros()
            val scale = max(1, stripped.scale())
            val approxScale = (scale / index) + 1
            try {
                BigDecimal.ONE.divide(BigDecimal.TEN.pow(approxScale), mathContext)
            } catch (_: Exception) {
                BigDecimal("0.5")
            }
        }
        if (x.compareTo(BigDecimal.ZERO) == 0) {
            x = BigDecimal("0.5")
        }

        val nBig = BigDecimal.valueOf(index.toLong())
        val nMinus1 = BigDecimal.valueOf((index - 1).toLong())
        val tolerance = BigDecimal("1E-75")

        for (iter in 0 until 200) {
            val xPow = x.pow(index - 1, mathContext)
            val term1 = nMinus1.multiply(x, mathContext)
            val term2 = value.divide(xPow, mathContext)
            val xNext = term1.add(term2, mathContext).divide(nBig, mathContext)

            val diff = xNext.subtract(x).abs()
            if (diff.compareTo(tolerance) < 0) {
                return xNext
            }
            x = xNext
        }

        throw ArithmeticException("Unable to calculate this root at the selected precision.")
    }
}
