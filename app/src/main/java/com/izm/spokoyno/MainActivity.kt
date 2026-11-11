package com.izm.spokoyno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.izm.spokoyno.data.local.AppDatabase
import com.izm.spokoyno.data.local.UserPreferences
import com.izm.spokoyno.ui.theme.SpokoynoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем канал уведомлений при старте
        NotificationHelper.createNotificationChannel(this)
        setContent {
            val context = LocalContext.current
            val prefs = remember { UserPreferences(context) }
            val themeSetting by prefs.theme.collectAsState(initial = 0)

            // Логика выбора темы: 0 - системная, 1 - светлая, 2 - темная
            val darkTheme = when (themeSetting) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            SpokoynoTheme(darkTheme = darkTheme) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    // Смотрим, есть ли пользователь
    val user by db.userDao().getUser().collectAsState(initial = null)
    var isLoading by remember { mutableStateOf(true) }

    // Ждем, чтобы база успела ответить (предотвращаем мигание экрана)
    LaunchedEffect(user) {
        if (user != null || isLoading) {
            isLoading = false
        }
    }

    if (isLoading) {
        // Экран загрузки
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Если пользователя нет, стартовый экран - Профиль
    val startDestination = if (user == null) Screen.Profile.route else Screen.TestsList.route

    // Блокируем кнопку "Назад", если мы на экране профиля и пользователя еще нет
    BackHandler(enabled = user == null) {
        // Ничего не делаем, чтобы не закрыть приложение
    }

    Scaffold(
        bottomBar = {
            // Показываем нижнее меню ТОЛЬКО если мы не на экране профиля
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute != Screen.Profile.route) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(id = screen.titleResId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Экран профиля
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onProfileSaved = {
                        // Если мы на стартовом экране (первый запуск) — едем в тесты
                        if (user == null) {
                            navController.navigate(Screen.TestsList.route) {
                                popUpTo(Screen.Profile.route) { inclusive = true }
                            }
                        } else {
                            // Если мы пришли из настроек — просто возвращаемся назад
                            navController.popBackStack()
                        }
                    }
                )
            }

            // Экран тестов
            composable(Screen.TestsList.route) {
                TestsListScreen(onTestClick = { testId ->
                    navController.navigate(Screen.TestPassing.createRoute(testId))
                })
            }

            // Прохождение
            composable(
                route = Screen.TestPassing.route,
                arguments = listOf(navArgument("testId") { type = NavType.IntType })
            ) { backStackEntry ->
                val testId = backStackEntry.arguments?.getInt("testId") ?: 0
                TestPassingScreen(testId = testId, onTestFinished = { id ->
                    navController.navigate(Screen.TestResult.createRoute(id))
                })
            }

            // Результат
            composable(
                route = Screen.TestResult.route,
                arguments = listOf(navArgument("resultId") { type = NavType.IntType })
            ) { backStackEntry ->
                val resultId = backStackEntry.arguments?.getInt("resultId") ?: 0
                TestResultScreen(resultId = resultId,
                    onGoToTests = { navController.navigate(Screen.TestsList.route) { popUpTo(Screen.TestsList.route) { inclusive = true } } },
                    onGoToHistory = { navController.navigate(Screen.ResultsHistory.route) { popUpTo(Screen.TestsList.route) } }
                )
            }

            // История
            composable(Screen.ResultsHistory.route) { ResultsHistoryScreen() }

            // Настройки
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onEditProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
        }
    }
}