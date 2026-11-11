package com.izm.spokoyno

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.izm.spokoyno.data.local.AppDatabase
import com.izm.spokoyno.data.local.QuestionEntity
import com.izm.spokoyno.data.local.QuestionOptionEntity
import com.izm.spokoyno.data.local.TestEntity
import com.izm.spokoyno.network.NetworkClient
import kotlinx.coroutines.launch

@Composable
fun TestsListScreen(onTestClick: (Int) -> Unit) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val tests by db.testDao().getAllTests().collectAsState(initial = emptyList())
    val user by db.userDao().getUser().collectAsState(initial = null)

    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    fun loadFromNetwork() {
        scope.launch {
            isLoading = true
            errorText = null
            try {
                val response = NetworkClient.api.getRemoteTests()

                db.testDao().deleteAllTests()

                response.tests.forEach { dto ->
                    // Превращаем интерпретации в JSON-строку для Room
                    val interpretationsJson = Gson().toJson(dto.interpretations)

                    val testEntity = TestEntity(
                        id = dto.id,
                        title = dto.title,
                        description = dto.description,
                        category = dto.category,
                        instruction = dto.instruction,
                        interpretationsJson = interpretationsJson
                    )
                    db.testDao().insertTests(listOf(testEntity))

                    val questionEntities = dto.questions.map { qDto ->
                        QuestionEntity(
                            testId = dto.id,
                            text = qDto.text,
                            orderNumber = qDto.number
                        )
                    }
                    val insertedQuestionIds = db.questionDao().insertQuestions(questionEntities)

                    val optionEntities = mutableListOf<QuestionOptionEntity>()
                    dto.questions.forEachIndexed { index, qDto ->
                        val realQuestionId = insertedQuestionIds[index].toInt()
                        qDto.options.forEach { optDto ->
                            optionEntities.add(
                                QuestionOptionEntity(
                                    questionId = realQuestionId,
                                    text = optDto.text,
                                    score = optDto.score
                                )
                            )
                        }
                    }
                    db.questionOptionDao().insertOptions(optionEntities)
                }

                isLoading = false
            } catch (e: Exception) {
                errorText = "Ошибка загрузки: ${e.message}"
                isLoading = false
            }
        }
    }

    LaunchedEffect(tests.size) {
        if (tests.isEmpty() && !isLoading && errorText == null) {
            loadFromNetwork()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (user != null) {
            Text(
                text = "Здравствуй, ${user?.name}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Как себя чувствуешь сегодня?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Доступные тесты", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { loadFromNetwork() }, enabled = !isLoading) {
                Text("Обновить")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorText != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorText!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { loadFromNetwork() }) {
                        Text("Повторить")
                    }
                }
            }
            else -> {
                LazyColumn {
                    items(tests) { test ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = { onTestClick(test.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(test.title, style = MaterialTheme.typography.titleMedium)
                                Text(test.description, style = MaterialTheme.typography.bodyMedium)
                                Text("Категория: ${test.category}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}