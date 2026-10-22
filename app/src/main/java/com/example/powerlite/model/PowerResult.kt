package com.example.powerlite.model

import java.math.BigDecimal
import java.math.BigInteger

data class PowerResult(
    val base: BigDecimal,
    val exponent: BigInteger,
    val result: BigDecimal,
    val isExact: Boolean,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long = System.currentTimeMillis()
)
