package com.izm.spokoyno

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.izm.spokoyno.data.local.UserPreferences
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onEditProfileClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    // Состояния из DataStore
    val themeSetting by prefs.theme.collectAsState(initial = 0)

    // Локальные состояния для настроек уведомлений
    val savedRemindersEnabled by prefs.remindersEnabled.collectAsState(initial = false)
    val savedReminderFreq by prefs.reminderFrequency.collectAsState(initial = "weekly")
    val savedReminderTime by prefs.reminderTime.collectAsState(initial = "10:00")
    val savedReminderDay by prefs.reminderDay.collectAsState(initial = 1)

    var remindersEnabled by remember { mutableStateOf(savedRemindersEnabled) }
    var reminderFreq by remember { mutableStateOf(savedReminderFreq) }
    var reminderTime by remember { mutableStateOf(savedReminderTime) }
    var reminderDay by remember { mutableIntStateOf(savedReminderDay) } // <-- mutableIntStateOf

    // Синхронизация
    LaunchedEffect(savedRemindersEnabled) { remindersEnabled = savedRemindersEnabled }
    LaunchedEffect(savedReminderFreq) { reminderFreq = savedReminderFreq }
    LaunchedEffect(savedReminderTime) { reminderTime = savedReminderTime }
    LaunchedEffect(savedReminderDay) { reminderDay = savedReminderDay }

    // Состояния для меню
    var themeExpanded by remember { mutableStateOf(false) }
    var freqExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    val themeNames = listOf("Системная", "Светлая", "Тёмная")

    // БЕЗОПАСНЫЙ ЗАПРОС РАЗРЕШЕНИЯ
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationHelper.showNotification(context, "Спокойно", "Пора пройти тест и проверить свое состояние! 😌")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // <-- Скролл
    ) {
        Text("Настройки", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onEditProfileClick, modifier = Modifier.fillMaxWidth()) {
            Text("Редактировать данные профиля")
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- ВЫБОР ТЕМЫ ---
        Text("Оформление", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = themeExpanded,
            onExpandedChange = { themeExpanded = !themeExpanded }
        ) {
            OutlinedTextField(
                value = themeNames[themeSetting],
                onValueChange = {},
                readOnly = true,
                label = { Text("Тема приложения") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                themeNames.forEachIndexed { index, name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            scope.launch { prefs.setTheme(index) }
                            themeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- УВЕДОМЛЕНИЯ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Напоминания", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = remindersEnabled,
                onCheckedChange = { remindersEnabled = it }
            )
        }

        if (remindersEnabled) {
            Spacer(modifier = Modifier.height(16.dp))

            // Частота
            ExposedDropdownMenuBox(
                expanded = freqExpanded,
                onExpandedChange = { freqExpanded = !freqExpanded }
            ) {
                OutlinedTextField(
                    value = if (reminderFreq == "daily") "Ежедневно" else "Еженедельно",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Частота") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = freqExpanded, onDismissRequest = { freqExpanded = false }) {
                    DropdownMenuItem(text = { Text("Ежедневно") }, onClick = {
                        reminderFreq = "daily"
                        freqExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Еженедельно") }, onClick = {
                        reminderFreq = "weekly"
                        freqExpanded = false
                    })
                }
            }

            // День недели
            if (reminderFreq == "weekly") {
                Spacer(modifier = Modifier.height(8.dp))
                var dayExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = dayExpanded,
                    onExpandedChange = { dayExpanded = !dayExpanded }
                ) {
                    OutlinedTextField(
                        value = daysOfWeek[reminderDay - 1],
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("День недели") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                        daysOfWeek.forEachIndexed { index, day ->
                            DropdownMenuItem(text = { Text(day) }, onClick = {
                                reminderDay = index + 1
                                dayExpanded = false
                            })
                        }
                    }
                }
            }

            // Время
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true }
            ) {
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Время (нажмите для выбора)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- КНОПКА СОХРАНЕНИЯ НАСТРОЕК ---
            Button(
                onClick = {
                    scope.launch {
                        prefs.setRemindersEnabled(remindersEnabled)
                        prefs.setReminderFrequency(reminderFreq)
                        prefs.setReminderTime(reminderTime)
                        prefs.setReminderDay(reminderDay)

                        ReminderScheduler.schedule(
                            context = context,
                            enabled = remindersEnabled,
                            freq = reminderFreq,
                            time = reminderTime,
                            day = reminderDay
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить настройки напоминаний")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка ручной проверки (теперь безопасная)
            OutlinedButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            return@OutlinedButton
                        }
                    }
                    NotificationHelper.showNotification(
                        context,
                        "Спокойно",
                        "Пора пройти тест и проверить свое состояние! 😌"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Проверить уведомление сейчас")
            }
        }
    }

    // Вызов кастомного диалога с колесиками
    if (showTimePicker) {
        WheelTimePickerDialog(
            initialTime = reminderTime,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { newTime ->
                reminderTime = newTime
                showTimePicker = false
            }
        )
    }
}

/**
 * Кастомный диалог, внутри которого системные NumberPicker
 */
@Composable
fun WheelTimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val initialHour = initialTime.substringBefore(":").toIntOrNull() ?: 10
    val initialMinute = initialTime.substringAfter(":").toIntOrNull() ?: 0

    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.width(320.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Выберите время", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberPicker(
                        initialValue = selectedHour,
                        minValue = 0,
                        maxValue = 23,
                        onValueChange = { selectedHour = it },
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        ":",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    NumberPicker(
                        initialValue = selectedMinute,
                        minValue = 0,
                        maxValue = 59,
                        onValueChange = { selectedMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // Добавлен Locale.getDefault()
                        onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute))
                    }) {
                        Text("ОК")
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(
    initialValue: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            android.widget.NumberPicker(context).apply {
                this.minValue = minValue
                this.maxValue = maxValue
                this.value = initialValue
                this.wrapSelectorWheel = true
                // Добавлен Locale.getDefault()
                this.setFormatter { String.format(Locale.getDefault(), "%02d", it) }

                this.setOnValueChangedListener { _, _, newVal ->
                    onValueChange(newVal)
                }
            }
        },
        update = { picker ->
            if (picker.value != initialValue) {
                picker.value = initialValue
            }
        },
        modifier = modifier.height(180.dp)
    )
}