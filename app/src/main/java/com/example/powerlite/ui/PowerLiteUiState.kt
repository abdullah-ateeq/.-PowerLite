package com.example.powerlite.ui

import com.example.powerlite.model.AppSettings
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.PowerLiteHistory
import com.example.powerlite.model.PowerResult
import com.example.powerlite.model.RootResult

data class PowerLiteUiState(
    val mode: OperationType = OperationType.POWER,

    val baseInput: String = "",
    val exponentInput: String = "",

    val rootValueInput: String = "",
    val rootIndexInput: String = "2",

    val powerResult: PowerResult? = null,
    val rootResult: RootResult? = null,

    val history: List<PowerLiteHistory> = emptyList(),

    val errorMessage: String? = null,

    val isCalculating: Boolean = false,

    val settings: AppSettings = AppSettings(),

    val isCalculationStepsExpanded: Boolean = false,

    val showSaveSuccessToast: Boolean = false
)
