package com.example.powerlite

import com.example.powerlite.data.repository.SettingsRepository
import com.example.powerlite.model.AppSettings
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.RoundingModeOption
import com.example.powerlite.model.ThemeMode
import com.example.powerlite.testutil.FakeSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var repository: SettingsRepository

    @Before
    fun setup() {
        fakePrefs = FakeSharedPreferences()
        repository = SettingsRepository(context = null, prefs = fakePrefs)
    }

    @Test
    fun testDefaultSettings() {
        val settings = repository.getSettings()
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertTrue(settings.dynamicColor)
        assertTrue(settings.hapticFeedback)
        assertTrue(settings.animationsEnabled)
        assertTrue(settings.confirmDelete)
        assertEquals(10, settings.decimalPlaces)
        assertEquals(RoundingModeOption.HALF_UP, settings.roundingMode)
    }

    @Test
    fun testSettingsPersistence() {
        val modified = AppSettings(
            themeMode = ThemeMode.DARK,
            dynamicColor = false,
            hapticFeedback = false,
            animationsEnabled = false,
            confirmDelete = false,
            decimalPlaces = 4,
            roundingMode = RoundingModeOption.HALF_EVEN
        )

        repository.saveSettings(modified)

        val retrieved = repository.getSettings()
        assertEquals(ThemeMode.DARK, retrieved.themeMode)
        assertEquals(false, retrieved.dynamicColor)
        assertEquals(false, retrieved.hapticFeedback)
        assertEquals(false, retrieved.animationsEnabled)
        assertEquals(false, retrieved.confirmDelete)
        assertEquals(4, retrieved.decimalPlaces)
        assertEquals(RoundingModeOption.HALF_EVEN, retrieved.roundingMode)
    }

    @Test
    fun testSettingsReset() {
        val custom = AppSettings(
            themeMode = ThemeMode.LIGHT,
            decimalPlaces = 20,
            roundingMode = RoundingModeOption.CEILING
        )
        repository.saveSettings(custom)

        val reset = repository.resetSettings()
        assertEquals(ThemeMode.SYSTEM, reset.themeMode)
        assertTrue(reset.dynamicColor)
        assertTrue(reset.hapticFeedback)
        assertTrue(reset.animationsEnabled)
        assertTrue(reset.confirmDelete)
        assertEquals(10, reset.decimalPlaces)
        assertEquals(RoundingModeOption.HALF_UP, reset.roundingMode)

        val reloaded = repository.getSettings()
        assertEquals(ThemeMode.SYSTEM, reloaded.themeMode)
    }

    @Test
    fun testOperationAndInputsPersistence() {
        repository.saveLastOperation(OperationType.ROOT)
        assertEquals(OperationType.ROOT, repository.getLastOperation())

        repository.saveLastPowerInputs("12", "2")
        val powerInputs = repository.getLastPowerInputs()
        assertEquals("12", powerInputs.first)
        assertEquals("2", powerInputs.second)

        repository.saveLastRootInputs("144", "2")
        val rootInputs = repository.getLastRootInputs()
        assertEquals("144", rootInputs.first)
        assertEquals("2", rootInputs.second)
    }
}
