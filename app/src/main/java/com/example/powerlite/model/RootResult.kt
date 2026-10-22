package com.example.powerlite.model

import java.math.BigDecimal

data class RootResult(
    val value: BigDecimal,
    val rootIndex: Int,
    val result: BigDecimal?,
    val isExact: Boolean,
    val isReal: Boolean,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long = System.currentTimeMillis()
)
