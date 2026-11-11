package com.izm.spokoyno.network

import retrofit2.http.GET

// --- Модели данных ---
data class TestsResponse(
    val version: Int,
    val disclaimer: String,
    val tests: List<TestDto>
)

data class TestDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val instruction: String,
    val estimatedMinutes: Int,
    val questions: List<QuestionDto>,
    val interpretations: List<InterpretationDto>
)

data class QuestionDto(
    val number: Int,
    val text: String,
    val options: List<OptionDto>
)

data class OptionDto(
    val id: Int,
    val text: String,
    val score: Int,
    val trait: String? = null
)

data class InterpretationDto(
    val min: Int,
    val max: Int,
    val text: String
)

// --- Интерфейс Retrofit ---
interface TestApi {
    @GET("VikaIz/psihi/main/api/tests.json")
    suspend fun getRemoteTests(): TestsResponse
}