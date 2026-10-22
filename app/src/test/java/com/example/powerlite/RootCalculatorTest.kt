package com.example.powerlite

import com.example.powerlite.domain.math.DecimalRootCalculator
import com.example.powerlite.domain.math.IntegerRootCalculator
import com.example.powerlite.domain.math.RootCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

class RootCalculatorTest {

    private val integerRootCalculator = IntegerRootCalculator()
    private val decimalRootCalculator = DecimalRootCalculator(integerRootCalculator)
    private val rootCalculator = RootCalculator(integerRootCalculator, decimalRootCalculator)

    @Test
    fun testIntegerNthRoot() {
        assertEquals(BigInteger("4"), integerRootCalculator.integerNthRoot(BigInteger("16"), 2))
        assertEquals(BigInteger("4"), integerRootCalculator.integerNthRoot(BigInteger("17"), 2))
        assertEquals(BigInteger("3"), integerRootCalculator.integerNthRoot(BigInteger("27"), 3))
        assertEquals(BigInteger("3"), integerRootCalculator.integerNthRoot(BigInteger("28"), 3))
        assertEquals(BigInteger("2"), integerRootCalculator.integerNthRoot(BigInteger("32"), 5))
        assertEquals(BigInteger("0"), integerRootCalculator.integerNthRoot(BigInteger("0"), 3))
        assertEquals(BigInteger("100"), integerRootCalculator.integerNthRoot(BigInteger("100"), 1))
    }

    @Test
    fun testSquareRoot_exact() {
        val res16 = rootCalculator.calculateRoot(BigDecimal("16"), 2).getOrThrow()
        assertEquals(0, res16.result?.compareTo(BigDecimal("4")))
        assertTrue(res16.isExact)
        assertTrue(res16.isReal)

        val res25 = rootCalculator.calculateRoot(BigDecimal("25"), 2).getOrThrow()
        assertEquals(0, res25.result?.compareTo(BigDecimal("5")))
        assertTrue(res25.isExact)
    }

    @Test
    fun testCubeRoot_exact() {
        val res27 = rootCalculator.calculateRoot(BigDecimal("27"), 3).getOrThrow()
        assertEquals(0, res27.result?.compareTo(BigDecimal("3")))
        assertTrue(res27.isExact)

        val res125 = rootCalculator.calculateRoot(BigDecimal("125"), 3).getOrThrow()
        assertEquals(0, res125.result?.compareTo(BigDecimal("5")))
        assertTrue(res125.isExact)
    }

    @Test
    fun testFourthAndFifthRoot_exact() {
        val res81 = rootCalculator.calculateRoot(BigDecimal("81"), 4).getOrThrow()
        assertEquals(0, res81.result?.compareTo(BigDecimal("3")))
        assertTrue(res81.isExact)

        val res32 = rootCalculator.calculateRoot(BigDecimal("32"), 5).getOrThrow()
        assertEquals(0, res32.result?.compareTo(BigDecimal("2")))
        assertTrue(res32.isExact)
    }

    @Test
    fun testRootIndexOne() {
        val res = rootCalculator.calculateRoot(BigDecimal("25"), 1).getOrThrow()
        assertEquals(0, res.result?.compareTo(BigDecimal("25")))
        assertTrue(res.isExact)
    }

    @Test
    fun testRootOfZero() {
        val res1 = rootCalculator.calculateRoot(BigDecimal("0"), 2).getOrThrow()
        assertEquals(0, res1.result?.compareTo(BigDecimal("0")))
        assertTrue(res1.isExact)

        val res2 = rootCalculator.calculateRoot(BigDecimal("0"), 3).getOrThrow()
        assertEquals(0, res2.result?.compareTo(BigDecimal("0")))
        assertTrue(res2.isExact)

        val res3 = rootCalculator.calculateRoot(BigDecimal("0"), 10).getOrThrow()
        assertEquals(0, res3.result?.compareTo(BigDecimal("0")))
        assertTrue(res3.isExact)
    }

    @Test
    fun testRootOfOne() {
        val res1 = rootCalculator.calculateRoot(BigDecimal("1"), 2).getOrThrow()
        assertEquals(0, res1.result?.compareTo(BigDecimal("1")))
        assertTrue(res1.isExact)

        val res2 = rootCalculator.calculateRoot(BigDecimal("1"), 3).getOrThrow()
        assertEquals(0, res2.result?.compareTo(BigDecimal("1")))
        assertTrue(res2.isExact)

        val res3 = rootCalculator.calculateRoot(BigDecimal("1"), 10).getOrThrow()
        assertEquals(0, res3.result?.compareTo(BigDecimal("1")))
        assertTrue(res3.isExact)
    }

    @Test
    fun testNegativeOddRoot() {
        val res27 = rootCalculator.calculateRoot(BigDecimal("-27"), 3).getOrThrow()
        assertEquals(0, res27.result?.compareTo(BigDecimal("-3")))
        assertTrue(res27.isExact)
        assertTrue(res27.isReal)

        val res32 = rootCalculator.calculateRoot(BigDecimal("-32"), 5).getOrThrow()
        assertEquals(0, res32.result?.compareTo(BigDecimal("-2")))
        assertTrue(res32.isExact)
        assertTrue(res32.isReal)
    }

    @Test
    fun testNegativeEvenRoot_noRealResult() {
        val res = rootCalculator.calculateRoot(BigDecimal("-16"), 2).getOrThrow()
        assertNull(res.result)
        assertFalse(res.isReal)
        assertFalse(res.isExact)
        assertTrue(res.calculationSteps.any { it.result == "No real result" })
    }

    @Test
    fun testNonExactRoot_newtonRaphson() {
        val res = rootCalculator.calculateRoot(BigDecimal("2"), 2).getOrThrow()
        assertNotNull(res.result)
        assertFalse(res.isExact)
        assertTrue(res.isReal)

        // Verify high precision sqrt(2) ≈ 1.414213562373095...
        val formatted10 = res.result!!.setScale(10, RoundingMode.HALF_UP).toPlainString()
        assertEquals("1.4142135624", formatted10)

        // Verify cube root of 10
        val cube10 = rootCalculator.calculateRoot(BigDecimal("10"), 3).getOrThrow()
        assertNotNull(cube10.result)
        assertFalse(cube10.isExact)
        val cubeFormatted = cube10.result!!.setScale(6, RoundingMode.HALF_UP).toPlainString()
        assertEquals("2.154435", cubeFormatted)
    }

    @Test
    fun testExactDecimalRoot() {
        // sqrt(0.25) = 0.5
        val res1 = rootCalculator.calculateRoot(BigDecimal("0.25"), 2).getOrThrow()
        assertEquals(0, res1.result?.compareTo(BigDecimal("0.5")))
        assertTrue(res1.isExact)

        // cbrt(0.125) = 0.5
        val res2 = rootCalculator.calculateRoot(BigDecimal("0.125"), 3).getOrThrow()
        assertEquals(0, res2.result?.compareTo(BigDecimal("0.5")))
        assertTrue(res2.isExact)
    }

    @Test
    fun testLargeRoot() {
        // 10^20 square root is 10^10
        val largeVal = BigDecimal("100000000000000000000") // 20 zeros
        val res = rootCalculator.calculateRoot(largeVal, 2).getOrThrow()
        val expected = BigDecimal("10000000000") // 10 zeros
        assertEquals(0, res.result?.compareTo(expected))
        assertTrue(res.isExact)
    }

    @Test
    fun testDecimalNthRootDirect() {
        val mc = MathContext(80, RoundingMode.HALF_UP)
        val root = decimalRootCalculator.decimalNthRoot(BigDecimal("2"), 2, mc)
        val squared = root.multiply(root, mc)
        // Check squared is 2 to 70+ decimal places
        val diff = squared.subtract(BigDecimal("2")).abs()
        assertTrue(diff < BigDecimal("1E-70"))
    }
}
