package com.example.powerlite.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.powerlite.model.RoundingModeOption
import com.example.powerlite.model.ThemeMode
import com.example.powerlite.ui.PowerLiteUiState
import com.example.powerlite.ui.PowerLiteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PowerLiteViewModel,
    uiState: PowerLiteUiState,
    onNavigateBack: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val decimalOptions = listOf(0, 2, 4, 6, 8, 10, 12, 16, 20, 30, 50)
    var decimalDropdownExpanded by remember { mutableStateOf(false) }
    var roundingDropdownExpanded by remember { mutableStateOf(false) }
    var themeDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // APPEARANCE SECTION
            SettingsSectionHeader(title = "Appearance")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Theme Mode Dropdown
                    ExposedDropdownMenuBox(
                        expanded = themeDropdownExpanded,
                        onExpandedChange = { themeDropdownExpanded = !themeDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.settings.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Theme") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = themeDropdownExpanded,
                            onDismissRequest = { themeDropdownExpanded = false }
                        ) {
                            ThemeMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.updateSettings(uiState.settings.copy(themeMode = mode))
                                        themeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Color Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dynamic Color", fontWeight = FontWeight.Medium)
                            Text(
                                "Use wallpaper-based colors on Android 12+",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.settings.dynamicColor,
                            onCheckedChange = {
                                viewModel.updateSettings(uiState.settings.copy(dynamicColor = it))
                            }
                        )
                    }
                }
            }

            // BEHAVIOR SECTION
            SettingsSectionHeader(title = "Behavior")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Haptic Feedback Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Haptic Feedback", fontWeight = FontWeight.Medium)
                            Text(
                                "Vibrate on button taps and results",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.settings.hapticFeedback,
                            onCheckedChange = {
                                viewModel.updateSettings(uiState.settings.copy(hapticFeedback = it))
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Animations Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Animations", fontWeight = FontWeight.Medium)
                            Text(
                                "Smooth UI transitions and result animations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.settings.animationsEnabled,
                            onCheckedChange = {
                                viewModel.updateSettings(uiState.settings.copy(animationsEnabled = it))
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Confirm Delete Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Confirm Delete", fontWeight = FontWeight.Medium)
                            Text(
                                "Show confirmation before deleting history items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.settings.confirmDelete,
                            onCheckedChange = {
                                viewModel.updateSettings(uiState.settings.copy(confirmDelete = it))
                            }
                        )
                    }
                }
            }

            // CALCULATION SECTION
            SettingsSectionHeader(title = "Calculation")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Decimal Places Dropdown
                    ExposedDropdownMenuBox(
                        expanded = decimalDropdownExpanded,
                        onExpandedChange = { decimalDropdownExpanded = !decimalDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${uiState.settings.decimalPlaces} decimal places",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Display Decimal Places") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = decimalDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = decimalDropdownExpanded,
                            onDismissRequest = { decimalDropdownExpanded = false }
                        ) {
                            decimalOptions.forEach { places ->
                                DropdownMenuItem(
                                    text = { Text("$places decimal places") },
                                    onClick = {
                                        viewModel.updateSettings(uiState.settings.copy(decimalPlaces = places))
                                        decimalDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rounding Mode Dropdown
                    ExposedDropdownMenuBox(
                        expanded = roundingDropdownExpanded,
                        onExpandedChange = { roundingDropdownExpanded = !roundingDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.settings.roundingMode.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rounding Mode") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundingDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = roundingDropdownExpanded,
                            onDismissRequest = { roundingDropdownExpanded = false }
                        ) {
                            RoundingModeOption.values().forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.displayName) },
                                    onClick = {
                                        viewModel.updateSettings(uiState.settings.copy(roundingMode = opt))
                                        roundingDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // DATA SECTION
            SettingsSectionHeader(title = "Data")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearHistoryDialog = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Clear History", fontWeight = FontWeight.Medium)
                            Text(
                                "Delete all saved calculation history",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showClearHistoryDialog = true }) {
                            Text("Clear", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Reset Settings", fontWeight = FontWeight.Medium)
                            Text(
                                "Restore all settings to default values",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showResetDialog = true }) {
                            Text("Reset", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("All appearance, behavior, and calculation settings will be restored to defaults. History will remain intact.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History?") },
            text = { Text("Are you sure you want to clear all saved calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}
