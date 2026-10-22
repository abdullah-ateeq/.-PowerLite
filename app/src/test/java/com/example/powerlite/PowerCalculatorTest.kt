package com.example.powerlite

import com.example.powerlite.domain.math.PowerCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class PowerCalculatorTest {

    private val powerCalculator = PowerCalculator()

    @Test
    fun testBasicPositivePower() {
        val result = powerCalculator.calculatePower(BigDecimal("5"), BigInteger("3")).getOrThrow()
        assertEquals(0, result.result.compareTo(BigDecimal("125")))
        assertTrue(result.isExact)
    }

    @Test
    fun testSquareAndCube() {
        val squareRes = powerCalculator.calculatePower(BigDecimal("12"), BigInteger("2")).getOrThrow()
        assertEquals(0, squareRes.result.compareTo(BigDecimal("144")))
        assertTrue(squareRes.isExact)

        val cubeRes = powerCalculator.calculatePower(BigDecimal("4"), BigInteger("3")).getOrThrow()
        assertEquals(0, cubeRes.result.compareTo(BigDecimal("64")))
        assertTrue(cubeRes.isExact)
    }

    @Test
    fun testZeroExponent() {
        val res1 = powerCalculator.calculatePower(BigDecimal("5"), BigInteger("0")).getOrThrow()
        assertEquals(0, res1.result.compareTo(BigDecimal("1")))
        assertTrue(res1.isExact)

        val res2 = powerCalculator.calculatePower(BigDecimal("-10"), BigInteger("0")).getOrThrow()
        assertEquals(0, res2.result.compareTo(BigDecimal("1")))
        assertTrue(res2.isExact)

        val res3 = powerCalculator.calculatePower(BigDecimal("1.5"), BigInteger("0")).getOrThrow()
        assertEquals(0, res3.result.compareTo(BigDecimal("1")))
        assertTrue(res3.isExact)
    }

    @Test
    fun testZeroBasePositiveExponent() {
        val res1 = powerCalculator.calculatePower(BigDecimal("0"), BigInteger("1")).getOrThrow()
        assertEquals(0, res1.result.compareTo(BigDecimal("0")))

        val res2 = powerCalculator.calculatePower(BigDecimal("0"), BigInteger("2")).getOrThrow()
        assertEquals(0, res2.result.compareTo(BigDecimal("0")))

        val res10 = powerCalculator.calculatePower(BigDecimal("0"), BigInteger("10")).getOrThrow()
        assertEquals(0, res10.result.compareTo(BigDecimal("0")))
    }

    @Test
    fun testZeroToZero() {
        val res = powerCalculator.calculatePower(BigDecimal("0"), BigInteger("0"))
        assertTrue(res.isFailure)
        assertEquals("0⁰ is undefined in this calculator.", res.exceptionOrNull()?.message)
    }

    @Test
    fun testZeroToNegativeExponent() {
        val res = powerCalculator.calculatePower(BigDecimal("0"), BigInteger("-1"))
        assertTrue(res.isFailure)
        assertEquals("Cannot calculate: zero cannot be raised to a negative power.", res.exceptionOrNull()?.message)
    }

    @Test
    fun testNegativeExponent() {
        val res1 = powerCalculator.calculatePower(BigDecimal("2"), BigInteger("-3")).getOrThrow()
        assertEquals(0, res1.result.compareTo(BigDecimal("0.125")))
        assertTrue(res1.isExact)

        val res2 = powerCalculator.calculatePower(BigDecimal("10"), BigInteger("-2")).getOrThrow()
        assertEquals(0, res2.result.compareTo(BigDecimal("0.01")))
        assertTrue(res2.isExact)
    }

    @Test
    fun testNegativeBaseEvenExponent() {
        val res = powerCalculator.calculatePower(BigDecimal("-2"), BigInteger("2")).getOrThrow()
        assertEquals(0, res.result.compareTo(BigDecimal("4")))
        assertTrue(res.isExact)

        val res4 = powerCalculator.calculatePower(BigDecimal("-2"), BigInteger("4")).getOrThrow()
        assertEquals(0, res4.result.compareTo(BigDecimal("16")))
        assertTrue(res4.isExact)
    }

    @Test
    fun testNegativeBaseOddExponent() {
        val res = powerCalculator.calculatePower(BigDecimal("-2"), BigInteger("3")).getOrThrow()
        assertEquals(0, res.result.compareTo(BigDecimal("-8")))
        assertTrue(res.isExact)
    }

    @Test
    fun testNegativeBaseNegativeExponent() {
        val res1 = powerCalculator.calculatePower(BigDecimal("-2"), BigInteger("-3")).getOrThrow()
        assertEquals(0, res1.result.compareTo(BigDecimal("-0.125")))
        assertTrue(res1.isExact)

        val res2 = powerCalculator.calculatePower(BigDecimal("-2"), BigInteger("-2")).getOrThrow()
        assertEquals(0, res2.result.compareTo(BigDecimal("0.25")))
        assertTrue(res2.isExact)

        val res3 = powerCalculator.calculatePower(BigDecimal("-5"), BigInteger("-2")).getOrThrow()
        assertEquals(0, res3.result.compareTo(BigDecimal("0.04")))
        assertTrue(res3.isExact)
    }

    @Test
    fun testDecimalBase() {
        val res1 = powerCalculator.calculatePower(BigDecimal("1.5"), BigInteger("3")).getOrThrow()
        assertEquals(0, res1.result.compareTo(BigDecimal("3.375")))
        assertTrue(res1.isExact)

        val res2 = powerCalculator.calculatePower(BigDecimal("2.5"), BigInteger("2")).getOrThrow()
        assertEquals(0, res2.result.compareTo(BigDecimal("6.25")))
        assertTrue(res2.isExact)
    }

    @Test
    fun testLargeIntegerPower() {
        // Base larger than Long.MAX_VALUE (9223372036854775807)
        val largeBase = BigDecimal("10000000000000000000") // 10^19
        val exp = BigInteger("3")
        val res = powerCalculator.calculatePower(largeBase, exp).getOrThrow()
        val expected = BigDecimal("1000000000000000000000000000000000000000000000000000000000") // 10^57
        assertEquals(0, res.result.compareTo(expected))
        assertTrue(res.isExact)
    }

    @Test
    fun testLargeDecimalPower() {
        val decimalBase = BigDecimal("1.0000000000000000000000000000000000000002")
        val exp = BigInteger("2")
        val res = powerCalculator.calculatePower(decimalBase, exp).getOrThrow()
        val expected = decimalBase.multiply(decimalBase)
        assertEquals(0, res.result.compareTo(expected))
        assertTrue(res.isExact)
    }
}
