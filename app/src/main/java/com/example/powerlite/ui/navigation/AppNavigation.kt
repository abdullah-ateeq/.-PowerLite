package com.example.powerlite.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.powerlite.ui.PowerLiteViewModel
import com.example.powerlite.ui.about.AboutScreen
import com.example.powerlite.ui.history.HistoryScreen
import com.example.powerlite.ui.home.HomeScreen
import com.example.powerlite.ui.settings.SettingsScreen
import com.example.powerlite.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    viewModel: PowerLiteViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val animationsEnabled = uiState.settings.animationsEnabled
    val animDuration = if (animationsEnabled) 250 else 0

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            if (animationsEnabled) {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            } else {
                fadeIn(animationSpec = tween(0))
            }
        },
        exitTransition = {
            if (animationsEnabled) {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            } else {
                fadeOut(animationSpec = tween(0))
            }
        },
        popEnterTransition = {
            if (animationsEnabled) {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            } else {
                fadeIn(animationSpec = tween(0))
            }
        },
        popExitTransition = {
            if (animationsEnabled) {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            } else {
                fadeOut(animationSpec = tween(0))
            }
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                animationsEnabled = animationsEnabled
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
