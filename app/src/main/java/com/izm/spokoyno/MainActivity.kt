package com.izm.spokoyno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.izm.spokoyno.ui.theme.SpokoynoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpokoynoTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = stringResource(screen.titleResId)
                            )
                        },
                        label = { Text(stringResource(id = screen.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Возвращаемся к стартовому экрану и ОЧИЩАЕМ стек до него.
                                // Убрали saveState/restoreState, так как они ломают навигацию с аргументами
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true // Закрываем сам стартовый экран, чтобы начать с чистого листа
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TestsList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Список тестов
            composable(Screen.TestsList.route) {
                TestsListScreen(
                    onTestClick = { testId ->
                        navController.navigate(Screen.TestPassing.createRoute(testId))
                    }
                )
            }

            // Прохождение теста
            composable(
                route = Screen.TestPassing.route,
                arguments = listOf(navArgument("testId") { type = NavType.IntType })
            ) { backStackEntry ->
                val testId = backStackEntry.arguments?.getInt("testId") ?: 0
                TestPassingScreen(
                    testId = testId,
                    onTestFinished = { id ->
                        // Заменяем экран прохождения на экран результата,
                        // чтобы кнопка "Назад" не возвращала к завершенному тесту
                        navController.navigate(Screen.TestResult.createRoute(id)) {
                            popUpTo(Screen.TestPassing.route) { inclusive = true }
                        }
                    }
                )
            }

            // Результат теста
            composable(
                route = Screen.TestResult.route,
                arguments = listOf(navArgument("resultId") { type = NavType.IntType })
            ) { backStackEntry ->
                val resultId = backStackEntry.arguments?.getInt("resultId") ?: 0
                TestResultScreen(
                    resultId = resultId,
                    onGoToTests = {
                        // Четкий переход к тестам. Очищаем текущий стек.
                        navController.navigate(Screen.TestsList.route) {
                            popUpTo(Screen.TestsList.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onGoToHistory = {
                        // Четкий переход к истории. Очищаем текущий стек.
                        navController.navigate(Screen.ResultsHistory.route) {
                            popUpTo(Screen.TestsList.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // История
            composable(Screen.ResultsHistory.route) {
                ResultsHistoryScreen()
            }

            // Настройки
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}