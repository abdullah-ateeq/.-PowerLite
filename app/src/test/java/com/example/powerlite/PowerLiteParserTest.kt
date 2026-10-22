package com.example.powerlite

import com.example.powerlite.domain.PowerLiteParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class PowerLiteParserTest {

    private val parser = PowerLiteParser()

    @Test
    fun testDecimalParsing_validInputs() {
        val cases = mapOf(
            "100" to BigDecimal("100"),
            "-100" to BigDecimal("-100"),
            "+100" to BigDecimal("100"),
            "1.5" to BigDecimal("1.5"),
            "-2.75" to BigDecimal("-2.75"),
            "0.125" to BigDecimal("0.125"),
            "0" to BigDecimal("0"),
            " 25 " to BigDecimal("25")
        )

        for ((input, expected) in cases) {
            val result = parser.parseDecimal(input)
            assertTrue("Expected success for input '$input'", result.isSuccess)
            assertEquals("Expected $expected for '$input'", 0, result.getOrThrow().compareTo(expected))
        }
    }

    @Test
    fun testIntegerParsing_validInputs() {
        val cases = mapOf(
            "3" to BigInteger("3"),
            "0" to BigInteger("0"),
            "-2" to BigInteger("-2"),
            "10" to BigInteger("10"),
            "100" to BigInteger("100"),
            "-100" to BigInteger("-100"),
            "+50" to BigInteger("50"),
            " 42 " to BigInteger("42")
        )

        for ((input, expected) in cases) {
            val result = parser.parseInteger(input)
            assertTrue("Expected success for input '$input'", result.isSuccess)
            assertEquals("Expected $expected for '$input'", expected, result.getOrThrow())
        }
    }

    @Test
    fun testEmptyInput() {
        val decResult1 = parser.parseDecimal("")
        assertTrue(decResult1.isFailure)

        val decResult2 = parser.parseDecimal("   ")
        assertTrue(decResult2.isFailure)

        val intResult1 = parser.parseInteger("")
        assertTrue(intResult1.isFailure)

        val intResult2 = parser.parseInteger("   ")
        assertTrue(intResult2.isFailure)
    }

    @Test
    fun testInvalidInput() {
        val invalidDecimals = listOf("abc", "1.2.3", "--", "++", ".", "-.", "1 000")
        for (input in invalidDecimals) {
            val res = parser.parseDecimal(input)
            assertTrue("Expected failure for invalid decimal: '$input'", res.isFailure)
        }

        val invalidIntegers = listOf("abc", "1.5", "2.75", "1 000", "--", "++")
        for (input in invalidIntegers) {
            val res = parser.parseInteger(input)
            assertTrue("Expected failure for invalid integer: '$input'", res.isFailure)
        }
    }

    @Test
    fun testScientificNotationRejection() {
        val inputs = listOf("1e10", "2E5", "1.5e-3", "-2.5E4")
        for (input in inputs) {
            val decRes = parser.parseDecimal(input)
            assertTrue("Decimal should reject scientific notation: '$input'", decRes.isFailure)
            assertEquals("Enter a standard decimal number.", decRes.exceptionOrNull()?.message)

            val intRes = parser.parseInteger(input)
            assertTrue("Integer should reject scientific notation: '$input'", intRes.isFailure)
            assertEquals("Enter a standard decimal number.", intRes.exceptionOrNull()?.message)
        }
    }

    @Test
    fun testInputLengthLimit() {
        val longString = "1".repeat(501)
        val decRes = parser.parseDecimal(longString)
        assertTrue(decRes.isFailure)
        assertEquals("Number is too long. Maximum 500 characters.", decRes.exceptionOrNull()?.message)

        val intRes = parser.parseInteger(longString)
        assertTrue(intRes.isFailure)
        assertEquals("Number is too long. Maximum 500 characters.", intRes.exceptionOrNull()?.message)

        val exact500 = "1".repeat(500)
        val validRes = parser.parseInteger(exact500)
        assertTrue(validRes.isSuccess)
    }
}
