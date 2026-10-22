package com.example.powerlite.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.powerlite.data.repository.HistoryRepository
import com.example.powerlite.data.repository.SettingsRepository
import com.example.powerlite.domain.PowerLiteFormatter
import com.example.powerlite.domain.PowerLiteValidator
import com.example.powerlite.domain.math.PowerCalculator
import com.example.powerlite.domain.math.RootCalculator
import com.example.powerlite.model.AppSettings
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.PowerLiteHistory
import com.example.powerlite.util.HapticUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class PowerLiteViewModel(
    application: Application,
    private val powerCalculator: PowerCalculator = PowerCalculator(),
    private val rootCalculator: RootCalculator = RootCalculator(),
    private val validator: PowerLiteValidator = PowerLiteValidator(),
    private val formatter: PowerLiteFormatter = PowerLiteFormatter(),
    private val settingsRepository: SettingsRepository = SettingsRepository(application),
    private val historyRepository: HistoryRepository = HistoryRepository(application),
    private val hapticUtil: HapticUtil = HapticUtil(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PowerLiteUiState())
    val uiState: StateFlow<PowerLiteUiState> = _uiState.asStateFlow()

    private var calculationJob: Job? = null

    init {
        loadPersistedState()
    }

    private fun loadPersistedState() {
        val settings = settingsRepository.getSettings()
        val history = historyRepository.getHistory()
        val lastMode = settingsRepository.getLastOperation()
        val (lastBase, lastExp) = settingsRepository.getLastPowerInputs()
        val (lastRootVal, lastRootIdx) = settingsRepository.getLastRootInputs()

        _uiState.update {
            it.copy(
                settings = settings,
                history = history,
                mode = lastMode,
                baseInput = lastBase,
                exponentInput = lastExp,
                rootValueInput = lastRootVal,
                rootIndexInput = if (lastRootIdx.isEmpty()) "2" else lastRootIdx
            )
        }
    }

    fun setMode(mode: OperationType) {
        if (_uiState.value.mode != mode) {
            hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
            settingsRepository.saveLastOperation(mode)
            _uiState.update {
                it.copy(
                    mode = mode,
                    errorMessage = null
                )
            }
        }
    }

    fun setBaseInput(input: String) {
        _uiState.update { it.copy(baseInput = input, errorMessage = null) }
        settingsRepository.saveLastPowerInputs(input, _uiState.value.exponentInput)
    }

    fun setExponentInput(input: String) {
        _uiState.update { it.copy(exponentInput = input, errorMessage = null) }
        settingsRepository.saveLastPowerInputs(_uiState.value.baseInput, input)
    }

    fun setRootValueInput(input: String) {
        _uiState.update { it.copy(rootValueInput = input, errorMessage = null) }
        settingsRepository.saveLastRootInputs(input, _uiState.value.rootIndexInput)
    }

    fun setRootIndexInput(input: String) {
        _uiState.update { it.copy(rootIndexInput = input, errorMessage = null) }
        settingsRepository.saveLastRootInputs(_uiState.value.rootValueInput, input)
    }

    fun applyQuickPower(exponent: String) {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        _uiState.update { it.copy(exponentInput = exponent, errorMessage = null) }
        settingsRepository.saveLastPowerInputs(_uiState.value.baseInput, exponent)
        if (_uiState.value.baseInput.isNotBlank()) {
            calculate()
        }
    }

    fun applyQuickRoot(index: String) {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        _uiState.update { it.copy(rootIndexInput = index, errorMessage = null) }
        settingsRepository.saveLastRootInputs(_uiState.value.rootValueInput, index)
        if (_uiState.value.rootValueInput.isNotBlank()) {
            calculate()
        }
    }

    fun calculate() {
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isCalculating = true, errorMessage = null) }
            try {
                withTimeout(5000L) {
                    when (_uiState.value.mode) {
                        OperationType.POWER -> {
                            val valResult = validator.validatePowerInput(
                                _uiState.value.baseInput,
                                _uiState.value.exponentInput
                            )
                            if (valResult.isFailure) {
                                val err = valResult.exceptionOrNull()?.message ?: "Invalid input"
                                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update { it.copy(isCalculating = false, errorMessage = err) }
                                return@withTimeout
                            }
                            val (base, exp) = valResult.getOrThrow()
                            val calcResult = powerCalculator.calculatePower(base, exp)
                            if (calcResult.isFailure) {
                                val err = calcResult.exceptionOrNull()?.message ?: "Calculation error"
                                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update { it.copy(isCalculating = false, errorMessage = err) }
                            } else {
                                val res = calcResult.getOrThrow()
                                hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update {
                                    it.copy(
                                        isCalculating = false,
                                        powerResult = res,
                                        errorMessage = null
                                    )
                                }
                                settingsRepository.saveLastPowerInputs(
                                    _uiState.value.baseInput,
                                    _uiState.value.exponentInput
                                )
                            }
                        }
                        OperationType.ROOT -> {
                            val valResult = validator.validateRootInput(
                                _uiState.value.rootValueInput,
                                _uiState.value.rootIndexInput
                            )
                            if (valResult.isFailure) {
                                val err = valResult.exceptionOrNull()?.message ?: "Invalid input"
                                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update { it.copy(isCalculating = false, errorMessage = err) }
                                return@withTimeout
                            }
                            val (value, rootIndex) = valResult.getOrThrow()
                            val calcResult = rootCalculator.calculateRoot(value, rootIndex)
                            if (calcResult.isFailure) {
                                val err = calcResult.exceptionOrNull()?.message ?: "Calculation error"
                                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update { it.copy(isCalculating = false, errorMessage = err) }
                            } else {
                                val res = calcResult.getOrThrow()
                                hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
                                _uiState.update {
                                    it.copy(
                                        isCalculating = false,
                                        rootResult = res,
                                        errorMessage = null
                                    )
                                }
                                settingsRepository.saveLastRootInputs(
                                    _uiState.value.rootValueInput,
                                    _uiState.value.rootIndexInput
                                )
                            }
                        }
                    }
                }
            } catch (_: TimeoutCancellationException) {
                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = "Calculation took too long. Try a smaller value or lower precision."
                    )
                }
            } catch (_: CancellationException) {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = "Calculation cancelled."
                    )
                }
            } catch (e: Exception) {
                hapticUtil.performErrorHaptic(_uiState.value.settings.hapticFeedback)
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        errorMessage = e.message ?: "Calculation error"
                    )
                }
            }
        }
    }

    fun cancelCalculation() {
        calculationJob?.cancel()
    }

    fun clearCurrentMode() {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        _uiState.update { current ->
            when (current.mode) {
                OperationType.POWER -> {
                    settingsRepository.saveLastPowerInputs("", "")
                    current.copy(
                        baseInput = "",
                        exponentInput = "",
                        powerResult = null,
                        errorMessage = null,
                        isCalculationStepsExpanded = false
                    )
                }
                OperationType.ROOT -> {
                    settingsRepository.saveLastRootInputs("", "2")
                    current.copy(
                        rootValueInput = "",
                        rootIndexInput = "2",
                        rootResult = null,
                        errorMessage = null,
                        isCalculationStepsExpanded = false
                    )
                }
            }
        }
    }

    fun saveCurrentCalculation() {
        val state = _uiState.value
        val (first, second) = when (state.mode) {
            OperationType.POWER -> {
                if (state.powerResult == null || state.baseInput.isBlank() || state.exponentInput.isBlank()) return
                Pair(state.baseInput, state.exponentInput)
            }
            OperationType.ROOT -> {
                if (state.rootResult == null || state.rootValueInput.isBlank() || state.rootIndexInput.isBlank()) return
                Pair(state.rootValueInput, state.rootIndexInput)
            }
        }

        hapticUtil.performLightHaptic(state.settings.hapticFeedback)
        val updatedHistory = historyRepository.addHistory(state.mode, first, second)
        _uiState.update {
            it.copy(
                history = updatedHistory,
                showSaveSuccessToast = true
            )
        }
    }

    fun dismissSaveSuccessToast() {
        _uiState.update { it.copy(showSaveSuccessToast = false) }
    }

    fun restoreHistory(item: PowerLiteHistory) {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        when (item.operation) {
            OperationType.POWER -> {
                _uiState.update {
                    it.copy(
                        mode = OperationType.POWER,
                        baseInput = item.firstInput,
                        exponentInput = item.secondInput,
                        errorMessage = null
                    )
                }
                settingsRepository.saveLastOperation(OperationType.POWER)
                settingsRepository.saveLastPowerInputs(item.firstInput, item.secondInput)
            }
            OperationType.ROOT -> {
                _uiState.update {
                    it.copy(
                        mode = OperationType.ROOT,
                        rootValueInput = item.firstInput,
                        rootIndexInput = item.secondInput,
                        errorMessage = null
                    )
                }
                settingsRepository.saveLastOperation(OperationType.ROOT)
                settingsRepository.saveLastRootInputs(item.firstInput, item.secondInput)
            }
        }
        calculate()
    }

    fun deleteHistory(id: String) {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        val updated = historyRepository.deleteHistory(id)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearHistory() {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        historyRepository.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun updateSettings(settings: AppSettings) {
        settingsRepository.saveSettings(settings)
        _uiState.update { it.copy(settings = settings) }
    }

    fun resetSettings() {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        val defaultSettings = settingsRepository.resetSettings()
        _uiState.update { it.copy(settings = defaultSettings) }
    }

    fun toggleCalculationSteps() {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        _uiState.update { it.copy(isCalculationStepsExpanded = !it.isCalculationStepsExpanded) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun getCopyText(summary: Boolean = false): String {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        val state = _uiState.value
        val places = state.settings.decimalPlaces
        val mode = state.settings.roundingMode.roundingMode

        return when (state.mode) {
            OperationType.POWER -> {
                val res = state.powerResult ?: return ""
                if (summary) {
                    formatter.formatCopySummary(res, places, mode)
                } else {
                    formatter.formatCopyResult(res, places, mode)
                }
            }
            OperationType.ROOT -> {
                val res = state.rootResult ?: return ""
                if (summary) {
                    formatter.formatCopySummary(res, places, mode)
                } else {
                    formatter.formatCopyResult(res, places, mode)
                }
            }
        }
    }

    fun getShareText(): String {
        hapticUtil.performLightHaptic(_uiState.value.settings.hapticFeedback)
        val state = _uiState.value
        val places = state.settings.decimalPlaces
        val mode = state.settings.roundingMode.roundingMode

        return when (state.mode) {
            OperationType.POWER -> {
                val res = state.powerResult ?: return ""
                formatter.formatShareText(res, places, mode)
            }
            OperationType.ROOT -> {
                val res = state.rootResult ?: return ""
                formatter.formatShareText(res, places, mode)
            }
        }
    }
}
