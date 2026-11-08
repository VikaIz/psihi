package com.izm.spokoyno

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

// Здесь мы описываем наши три экрана и то, как они называются
sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector) {
    data object TestsList : Screen("tests_list", R.string.nav_tests, Icons.Default.List)
    data object TestPassing : Screen("test_passing", R.string.nav_passing, Icons.Default.PlayArrow)
    data object ResultsHistory : Screen("results_history", R.string.nav_history, Icons.Default.History)
}

// Список экранов для нижнего меню
val bottomNavItems = listOf(
    Screen.TestsList,
    Screen.TestPassing,
    Screen.ResultsHistory
)