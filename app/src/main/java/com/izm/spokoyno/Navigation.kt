package com.izm.spokoyno

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector) {

    // Разделы нижнего меню
    data object TestsList : Screen("tests_list", R.string.nav_tests, Icons.AutoMirrored.Filled.List)
    data object ResultsHistory : Screen("results_history", R.string.nav_history, Icons.Filled.History)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)

    // Глубокие экраны (открываются по логике, не из меню)
    data object TestPassing : Screen("test_passing/{testId}", R.string.nav_passing, Icons.Filled.PlayArrow) {
        fun createRoute(testId: Int) = "test_passing/$testId"
    }

    data object TestResult : Screen("test_result/{resultId}", R.string.nav_result, Icons.Filled.Star) {
        fun createRoute(resultId: Int) = "test_result/$resultId"
    }
}

val bottomNavItems = listOf(
    Screen.TestsList,
    Screen.ResultsHistory,
    Screen.Settings
)