package com.example.powerlite.model

data class PowerLiteHistory(
    val id: String,
    val operation: OperationType,
    val firstInput: String,
    val secondInput: String,
    val savedAt: Long
)
