package com.izm.spokoyno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.izm.spokoyno.data.local.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsHistoryScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    // Используем запрос с названиями
    val results by db.testResultDao().getAllResultsWithTitles().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("История результатов", style = MaterialTheme.typography.titleLarge)

            if (results.isNotEmpty()) {
                TextButton(onClick = {
                    scope.launch { db.testResultDao().clearHistory() }
                }) {
                    Text("Очистить", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(results) { result ->
                val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(result.date))
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Убрали ненужный Elvis-оператор
                        Text(result.testTitle, style = MaterialTheme.typography.titleMedium)
                        Text("Дата: $dateStr")
                        Text("Баллы: ${result.totalScore}")
                        Text("Оценка: ${result.interpretation}")
                    }
                }
            }
        }
    }
}