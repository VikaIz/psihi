package com.izm.spokoyno

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.izm.spokoyno.data.local.AppDatabase
import com.izm.spokoyno.data.local.QuestionEntity
import com.izm.spokoyno.data.local.QuestionOptionEntity
import com.izm.spokoyno.data.local.TestResultEntity
import com.izm.spokoyno.network.InterpretationDto
import kotlinx.coroutines.launch

@Composable
fun TestPassingScreen(testId: Int, onTestFinished: (Int) -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf<List<QuestionOptionEntity>>(emptyList()) }

    val answers = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(testId) {
        questions = db.questionDao().getQuestionsForTest(testId)
        if (questions.isNotEmpty()) {
            options = db.questionOptionDao().getOptionsForQuestion(questions[0].id)
        }
    }

    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            scope.launch {
                options = db.questionOptionDao().getOptionsForQuestion(questions[currentIndex].id)
            }
        } else {
            scope.launch {
                val totalScore = answers.values.sum()

                val testEntity = db.testDao().getTestById(testId)
                var interpretationText = "Баллов: $totalScore"

                if (testEntity != null) {
                    try {
                        val type = object : TypeToken<List<InterpretationDto>>() {}.type
                        val interpretations: List<InterpretationDto> = Gson().fromJson(testEntity.interpretationsJson, type)

                        val matched = interpretations.firstOrNull { totalScore in it.min..it.max }
                        if (matched != null) {
                            interpretationText = matched.text
                        }
                    } catch (_: Exception) {
                        // Оставляем дефолт, если ошибка парсинга (переменная _ означает, что мы ее игнорируем)
                    }
                }

                val result = TestResultEntity(
                    userId = 1,
                    testId = testId,
                    date = System.currentTimeMillis(),
                    totalScore = totalScore,
                    interpretation = interpretationText
                )

                val newId = db.testResultDao().insertResult(result)
                onTestFinished(newId.toInt())
            }
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Вопрос ${currentIndex + 1} из ${questions.size}",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentQuestion.text,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                val isSelected = answers[currentQuestion.id] == option.score
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            answers[currentQuestion.id] = option.score
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = option.text,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { nextQuestion() },
            enabled = answers.containsKey(currentQuestion.id),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (currentIndex < questions.size - 1) "Следующий вопрос" else "Завершить тест")
        }
    }
}