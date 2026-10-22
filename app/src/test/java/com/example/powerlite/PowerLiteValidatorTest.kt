package com.example.powerlite

import com.example.powerlite.domain.PowerLiteValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class PowerLiteValidatorTest {

    private val validator = PowerLiteValidator()

    @Test
    fun testPowerValidation_success() {
        val res = validator.validatePowerInput("5", "3")
        assertTrue(res.isSuccess)
        val (base, exp) = res.getOrThrow()
        assertEquals(0, base.compareTo(BigDecimal("5")))
        assertEquals(BigInteger("3"), exp)
    }

    @Test
    fun testPowerValidation_emptyInputs() {
        val res1 = validator.validatePowerInput("", "3")
        assertTrue(res1.isFailure)
        assertEquals("Enter a base value.", res1.exceptionOrNull()?.message)

        val res2 = validator.validatePowerInput("5", "")
        assertTrue(res2.isFailure)
        assertEquals("Enter an exponent.", res2.exceptionOrNull()?.message)
    }

    @Test
    fun testPowerValidation_invalidExponent() {
        val res1 = validator.validatePowerInput("5", "2.5")
        assertTrue(res1.isFailure)
        assertEquals("Exponent must be a whole number.", res1.exceptionOrNull()?.message)

        val res2 = validator.validatePowerInput("5", "abc")
        assertTrue(res2.isFailure)
    }

    @Test
    fun testPowerValidation_exponentLimit() {
        val validExp = validator.validatePowerInput("2", "100000")
        assertTrue(validExp.isSuccess)

        val tooLargeExp = validator.validatePowerInput("2", "100001")
        assertTrue(tooLargeExp.isFailure)
        assertEquals("Exponent is too large. Maximum absolute exponent is 100,000.", tooLargeExp.exceptionOrNull()?.message)
    }

    @Test
    fun testPowerValidation_outputSizeProtection() {
        // A huge base raised to large exponent that would exceed safe output digits
        val hugeBase = "1" + "0".repeat(50) // 10^50
        val exp = "3000" // (50 * 3000) = 150,000 digits > 100,000 digits
        val res = validator.validatePowerInput(hugeBase, exp)
        assertTrue(res.isFailure)
        assertEquals("Result is too large to display safely. Try a smaller exponent or base.", res.exceptionOrNull()?.message)
    }

    @Test
    fun testRootValidation_success() {
        val res = validator.validateRootInput("125", "3")
        assertTrue(res.isSuccess)
        val (value, rootIndex) = res.getOrThrow()
        assertEquals(0, value.compareTo(BigDecimal("125")))
        assertEquals(3, rootIndex)
    }

    @Test
    fun testRootValidation_emptyInputs() {
        val res1 = validator.validateRootInput("", "2")
        assertTrue(res1.isFailure)
        assertEquals("Enter a value.", res1.exceptionOrNull()?.message)

        val res2 = validator.validateRootInput("16", "")
        assertTrue(res2.isFailure)
        assertEquals("Enter a root index.", res2.exceptionOrNull()?.message)
    }

    @Test
    fun testRootValidation_invalidRootIndex() {
        val zeroIndex = validator.validateRootInput("16", "0")
        assertTrue(zeroIndex.isFailure)
        assertEquals("Root index must be a positive whole number.", zeroIndex.exceptionOrNull()?.message)

        val negIndex = validator.validateRootInput("16", "-2")
        assertTrue(negIndex.isFailure)
        assertEquals("Root index must be a positive whole number.", negIndex.exceptionOrNull()?.message)

        val decimalIndex = validator.validateRootInput("16", "1.5")
        assertTrue(decimalIndex.isFailure)
        assertEquals("Root index must be a positive whole number.", decimalIndex.exceptionOrNull()?.message)

        val abcIndex = validator.validateRootInput("16", "abc")
        assertTrue(abcIndex.isFailure)
        assertEquals("Root index must be a positive whole number.", abcIndex.exceptionOrNull()?.message)
    }

    @Test
    fun testRootValidation_indexOneSupported() {
        val res = validator.validateRootInput("25", "1")
        assertTrue(res.isSuccess)
        assertEquals(1, res.getOrThrow().second)
    }

    @Test
    fun testRootValidation_maximumRootIndex() {
        val valid1000 = validator.validateRootInput("100", "1000")
        assertTrue(valid1000.isSuccess)

        val invalid1001 = validator.validateRootInput("100", "1001")
        assertTrue(invalid1001.isFailure)
        assertEquals("Root index is too large. Maximum is 1000.", invalid1001.exceptionOrNull()?.message)
    }
}
