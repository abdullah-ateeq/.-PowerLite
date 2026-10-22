package com.example.powerlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.powerlite.ui.PowerLiteViewModel
import com.example.powerlite.ui.navigation.AppNavigation
import com.example.powerlite.ui.theme.PowerLiteTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PowerLiteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            PowerLiteTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
