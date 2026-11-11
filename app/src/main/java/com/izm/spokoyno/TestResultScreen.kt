package com.izm.spokoyno

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.izm.spokoyno.data.local.AppDatabase
import com.izm.spokoyno.data.local.TestResultEntity

@Composable
fun TestResultScreen(
    resultId: Int,
    onGoToTests: () -> Unit,
    onGoToHistory: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    var result by remember { mutableStateOf<TestResultEntity?>(null) }

    LaunchedEffect(resultId) {
        result = db.testResultDao().getResultById(resultId)
    }

    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Результат теста",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${result?.totalScore ?: 0}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "баллов",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = result?.interpretation ?: "Интерпретация отсутствует",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onGoToTests, modifier = Modifier.weight(1f)) {
                Text("К тестам")
            }
            OutlinedButton(onClick = onGoToHistory, modifier = Modifier.weight(1f)) {
                Text("В историю")
            }
        }
    }
}