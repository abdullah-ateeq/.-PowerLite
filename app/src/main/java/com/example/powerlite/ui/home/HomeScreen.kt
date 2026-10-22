package com.example.powerlite.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powerlite.domain.PowerLiteFormatter
import com.example.powerlite.model.OperationType
import com.example.powerlite.model.PowerResult
import com.example.powerlite.model.RootResult
import com.example.powerlite.ui.PowerLiteUiState
import com.example.powerlite.ui.PowerLiteViewModel
import com.example.powerlite.ui.theme.ApproxAmber
import com.example.powerlite.ui.theme.ApproxAmberContainer
import com.example.powerlite.ui.theme.ExactGreen
import com.example.powerlite.ui.theme.ExactGreenContainer
import com.example.powerlite.ui.theme.OnApproxAmber
import com.example.powerlite.ui.theme.OnExactGreen
import com.example.powerlite.util.SuperscriptUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PowerLiteViewModel,
    uiState: PowerLiteUiState,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    formatter: PowerLiteFormatter = remember { PowerLiteFormatter() }
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.showSaveSuccessToast) {
        if (uiState.showSaveSuccessToast) {
            Toast.makeText(context, "Calculation saved to history", Toast.LENGTH_SHORT).show()
            viewModel.dismissSaveSuccessToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PowerLite",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calculate powers and roots with precision.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.semantics { contentDescription = "View History" }
                    ) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.semantics { contentDescription = "Open Settings" }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(
                        onClick = onNavigateToAbout,
                        modifier = Modifier.semantics { contentDescription = "About PowerLite" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "About")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Segmented Control: [ POWER ]  [ ROOT ]
            OperationModeSelector(
                selectedMode = uiState.mode,
                onModeSelected = { viewModel.setMode(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Mode Section
            when (uiState.mode) {
                OperationType.POWER -> {
                    PowerModeSection(
                        uiState = uiState,
                        viewModel = viewModel,
                        focusManager = focusManager
                    )
                }
                OperationType.ROOT -> {
                    RootModeSection(
                        uiState = uiState,
                        viewModel = viewModel,
                        focusManager = focusManager
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Banner
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearErrorMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss error",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Calculation Progress & Cancel
            AnimatedVisibility(
                visible = uiState.isCalculating,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Calculating…",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.cancelCalculation() }
                        ) {
                            Text("CANCEL")
                        }
                    }
                }
            }

            // Result Card & Calculation Details
            val hasResult = (uiState.mode == OperationType.POWER && uiState.powerResult != null) ||
                    (uiState.mode == OperationType.ROOT && uiState.rootResult != null)

            AnimatedVisibility(
                visible = hasResult && !uiState.isCalculating,
                enter = fadeIn(animationSpec = tween(250)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.mode == OperationType.POWER && uiState.powerResult != null) {
                        PowerResultCard(
                            result = uiState.powerResult,
                            formatter = formatter,
                            decimalPlaces = uiState.settings.decimalPlaces,
                            roundingMode = uiState.settings.roundingMode.roundingMode
                        )
                    } else if (uiState.mode == OperationType.ROOT && uiState.rootResult != null) {
                        RootResultCard(
                            result = uiState.rootResult,
                            formatter = formatter,
                            decimalPlaces = uiState.settings.decimalPlaces,
                            roundingMode = uiState.settings.roundingMode.roundingMode
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Expandable Calculation Steps Card
                    CalculationStepsSection(
                        uiState = uiState,
                        onToggle = { viewModel.toggleCalculationSteps() }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons: [ COPY ] [ SHARE ] [ SAVE ] [ CLEAR ]
                    ActionButtonsRow(
                        onCopy = {
                            val text = viewModel.getCopyText(summary = false)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("PowerLite Result", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onCopySummary = {
                            val text = viewModel.getCopyText(summary = true)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("PowerLite Summary", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val text = viewModel.getShareText()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Calculation")
                            context.startActivity(shareIntent)
                        },
                        onSave = {
                            viewModel.saveCurrentCalculation()
                        },
                        onClear = {
                            viewModel.clearCurrentMode()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun OperationModeSelector(
    selectedMode: OperationType,
    onModeSelected: (OperationType) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val powerSelected = selectedMode == OperationType.POWER
            val rootSelected = selectedMode == OperationType.ROOT

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (powerSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onModeSelected(OperationType.POWER) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "POWER",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (powerSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (rootSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onModeSelected(OperationType.ROOT) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "ROOT",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (rootSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PowerModeSection(
    uiState: PowerLiteUiState,
    viewModel: PowerLiteViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Calculate Power",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // BASE INPUT
        Text(
            text = "BASE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.baseInput,
            onValueChange = { viewModel.setBaseInput(it) },
            placeholder = { Text("e.g. 5, -2, 1.5") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Base value." },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                if (uiState.baseInput.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setBaseInput("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear base")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // EXPONENT INPUT
        Text(
            text = "EXPONENT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.exponentInput,
            onValueChange = { viewModel.setExponentInput(it) },
            placeholder = { Text("Whole number: 3, -2, 0") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Power exponent." },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.calculate()
                }
            ),
            trailingIcon = {
                if (uiState.exponentInput.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setExponentInput("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear exponent")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // QUICK POWER BUTTONS: [ x² ]  [ x³ ]  [ x⁴ ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.exponentInput == "2",
                onClick = { viewModel.applyQuickPower("2") },
                label = { Text("x²", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = uiState.exponentInput == "3",
                onClick = { viewModel.applyQuickPower("3") },
                label = { Text("x³", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = uiState.exponentInput == "4",
                onClick = { viewModel.applyQuickPower("4") },
                label = { Text("x⁴", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // PRIMARY BUTTON: CALCULATE POWER
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.calculate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .semantics { contentDescription = "Calculate result." },
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "CALCULATE POWER",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun RootModeSection(
    uiState: PowerLiteUiState,
    viewModel: PowerLiteViewModel,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Calculate Root",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // VALUE INPUT
        Text(
            text = "VALUE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.rootValueInput,
            onValueChange = { viewModel.setRootValueInput(it) },
            placeholder = { Text("e.g. 125, 16, 2, -27") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Value to calculate the root of." },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                if (uiState.rootValueInput.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setRootValueInput("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear value")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ROOT INDEX INPUT
        Text(
            text = "ROOT INDEX",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = uiState.rootIndexInput,
            onValueChange = { viewModel.setRootIndexInput(it) },
            placeholder = { Text("Positive integer: 2, 3, 4") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Root index." },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.calculate()
                }
            ),
            trailingIcon = {
                if (uiState.rootIndexInput.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setRootIndexInput("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear index")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // QUICK ROOT BUTTONS: [ √ SQUARE ]  [ ∛ CUBE ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.rootIndexInput == "2",
                onClick = { viewModel.applyQuickRoot("2") },
                label = { Text("√ Square", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = uiState.rootIndexInput == "3",
                onClick = { viewModel.applyQuickRoot("3") },
                label = { Text("∛ Cube", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // PRIMARY BUTTON: CALCULATE ROOT
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.calculate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .semantics { contentDescription = "Calculate result." },
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "CALCULATE ROOT",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun PowerResultCard(
    result: PowerResult,
    formatter: PowerLiteFormatter,
    decimalPlaces: Int,
    roundingMode: java.math.RoundingMode
) {
    val expr = formatter.formatPowerExpression(result.base, result.exponent)
    val formattedResult = formatter.formatResult(result.result, result.isExact, decimalPlaces, roundingMode)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Power calculation result." },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = expr,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formattedResult,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (result.isExact) ExactGreenContainer else ApproxAmberContainer
            ) {
                Text(
                    text = if (result.isExact) "EXACT RESULT" else "APPROXIMATE RESULT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isExact) OnExactGreen else OnApproxAmber,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .semantics {
                            contentDescription = if (result.isExact) "Exact result." else "Approximate result."
                        }
                )
            }
        }
    }
}

@Composable
fun RootResultCard(
    result: RootResult,
    formatter: PowerLiteFormatter,
    decimalPlaces: Int,
    roundingMode: java.math.RoundingMode
) {
    val expr = formatter.formatRootExpression(result.value, result.rootIndex)
    val isReal = result.isReal && result.result != null
    val formattedResult = if (isReal) {
        formatter.formatResult(result.result!!, result.isExact, decimalPlaces, roundingMode)
    } else {
        "No real result"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Root calculation result." },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = expr,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            val symbol = when {
                !isReal -> "="
                result.isExact -> "="
                else -> "≈"
            }

            Text(
                text = if (isReal) "$symbol $formattedResult" else formattedResult,
                fontSize = if (isReal) 32.sp else 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (isReal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            val (badgeText, badgeBg, badgeTextColor) = when {
                !isReal -> Triple("NO REAL RESULT", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
                result.isExact -> Triple("EXACT RESULT", ExactGreenContainer, OnExactGreen)
                else -> Triple("APPROXIMATE RESULT", ApproxAmberContainer, OnApproxAmber)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .semantics { contentDescription = "$badgeText." }
                )
            }
        }
    }
}

@Composable
fun CalculationStepsSection(
    uiState: PowerLiteUiState,
    onToggle: () -> Unit
) {
    val steps = when (uiState.mode) {
        OperationType.POWER -> uiState.powerResult?.calculationSteps ?: emptyList()
        OperationType.ROOT -> uiState.rootResult?.calculationSteps ?: emptyList()
    }

    if (steps.isEmpty()) return

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SHOW CALCULATION",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (uiState.isCalculationStepsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (uiState.isCalculationStepsExpanded) "Collapse steps" else "Expand steps",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (uiState.isCalculationStepsExpanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    steps.forEach { step ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (step.label.isNotBlank()) {
                                Text(
                                    text = step.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (step.expression.isNotBlank()) {
                                Text(
                                    text = step.expression,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (step.result.isNotBlank() && step.result != step.expression) {
                                Text(
                                    text = step.result,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    onCopy: () -> Unit,
    onCopySummary: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onCopy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("COPY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            FilledTonalButton(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SHARE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            FilledTonalButton(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SAVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCopySummary,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("COPY SUMMARY", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("CLEAR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
