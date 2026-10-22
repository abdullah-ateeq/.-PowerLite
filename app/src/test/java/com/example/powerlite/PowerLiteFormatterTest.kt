package com.example.powerlite

import com.example.powerlite.domain.PowerLiteFormatter
import com.example.powerlite.model.PowerResult
import com.example.powerlite.model.RootResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class PowerLiteFormatterTest {

    private val formatter = PowerLiteFormatter()

    @Test
    fun testTrailingZeroRemoval() {
        // 5 instead of 5.0000000000
        val res1 = formatter.formatResult(BigDecimal("5.0000000000"), true, 10, RoundingMode.HALF_UP)
        assertEquals("5", res1)

        // 1.25 instead of 1.2500000000
        val res2 = formatter.formatResult(BigDecimal("1.2500000000"), true, 10, RoundingMode.HALF_UP)
        assertEquals("1.25", res2)
    }

    @Test
    fun testDecimalPlacesSettings() {
        val sqrt2 = BigDecimal("1.41421356237309504880")

        // 4 decimal places
        val formatted4 = formatter.formatResult(sqrt2, false, 4, RoundingMode.HALF_UP)
        assertEquals("1.4142", formatted4)

        // 10 decimal places
        val formatted10 = formatter.formatResult(sqrt2, false, 10, RoundingMode.HALF_UP)
        assertEquals("1.4142135624", formatted10)
    }

    @Test
    fun testRoundingModes() {
        // Test with 2.5555
        val testVal = BigDecimal("2.5555")

        assertEquals("2.56", formatter.formatResult(testVal, false, 2, RoundingMode.HALF_UP))
        assertEquals("2.56", formatter.formatResult(testVal, false, 2, RoundingMode.HALF_DOWN))
        assertEquals("2.56", formatter.formatResult(testVal, false, 2, RoundingMode.HALF_EVEN))
        assertEquals("2.56", formatter.formatResult(testVal, false, 2, RoundingMode.UP))
        assertEquals("2.55", formatter.formatResult(testVal, false, 2, RoundingMode.DOWN))
        assertEquals("2.56", formatter.formatResult(testVal, false, 2, RoundingMode.CEILING))
        assertEquals("2.55", formatter.formatResult(testVal, false, 2, RoundingMode.FLOOR))

        // Negative value rounding test
        val negVal = BigDecimal("-2.5555")
        assertEquals("-2.55", formatter.formatResult(negVal, false, 2, RoundingMode.CEILING))
        assertEquals("-2.56", formatter.formatResult(negVal, false, 2, RoundingMode.FLOOR))
    }

    @Test
    fun testCopyResultFormatting_power() {
        val powerResult = PowerResult(
            base = BigDecimal("5"),
            exponent = BigInteger("3"),
            result = BigDecimal("125"),
            isExact = true,
            calculationSteps = emptyList()
        )

        val copyText = formatter.formatCopyResult(powerResult, 10, RoundingMode.HALF_UP)
        assertTrue(copyText.contains("PowerLite"))
        assertTrue(copyText.contains("5^3 = 125"))
        assertTrue(copyText.contains("Exact Result"))
    }

    @Test
    fun testCopyResultFormatting_root() {
        val rootResult = RootResult(
            value = BigDecimal("2"),
            rootIndex = 2,
            result = BigDecimal("1.414213562373"),
            isExact = false,
            isReal = true,
            calculationSteps = emptyList()
        )

        val copyText = formatter.formatCopyResult(rootResult, 10, RoundingMode.HALF_UP)
        assertTrue(copyText.contains("PowerLite"))
        assertTrue(copyText.contains("√2 ≈ 1.4142135624"))
        assertTrue(copyText.contains("Approximate Result"))
    }

    @Test
    fun testCopySummaryFormatting_powerAndRoot() {
        val powerRes = PowerResult(
            base = BigDecimal("5"),
            exponent = BigInteger("3"),
            result = BigDecimal("125"),
            isExact = true,
            calculationSteps = emptyList()
        )
        val powerSummary = formatter.formatCopySummary(powerRes, 10, RoundingMode.HALF_UP)
        assertTrue(powerSummary.contains("Operation: Power"))
        assertTrue(powerSummary.contains("Base: 5"))
        assertTrue(powerSummary.contains("Exponent: 3"))
        assertTrue(powerSummary.contains("Result: 125"))
        assertTrue(powerSummary.contains("Status: Exact"))

        val rootRes = RootResult(
            value = BigDecimal("125"),
            rootIndex = 3,
            result = BigDecimal("5"),
            isExact = true,
            isReal = true,
            calculationSteps = emptyList()
        )
        val rootSummary = formatter.formatCopySummary(rootRes, 10, RoundingMode.HALF_UP)
        assertTrue(rootSummary.contains("Operation: Root"))
        assertTrue(rootSummary.contains("Value: 125"))
        assertTrue(rootSummary.contains("Root Index: 3"))
        assertTrue(rootSummary.contains("Result: 5"))
        assertTrue(rootSummary.contains("Status: Exact"))
    }

    @Test
    fun testShareFormatting() {
        val powerRes = PowerResult(
            base = BigDecimal("5"),
            exponent = BigInteger("3"),
            result = BigDecimal("125"),
            isExact = true,
            calculationSteps = emptyList()
        )
        val shareText = formatter.formatShareText(powerRes, 10, RoundingMode.HALF_UP)
        assertTrue(shareText.contains("PowerLite Calculation"))
        assertTrue(shareText.contains("5³ = 125"))
        assertTrue(shareText.contains("Exact Result"))
        assertTrue(shareText.contains("Calculated with PowerLite"))
    }
}
