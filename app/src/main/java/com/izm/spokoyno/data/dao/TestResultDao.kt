package com.izm.spokoyno.data.dao

import androidx.room.*
import com.izm.spokoyno.data.local.TestResultEntity
import kotlinx.coroutines.flow.Flow

// Класс для объединенных данных
data class TestResultWithTitle(
    val id: Int,
    val testId: Int,
    val date: Long,
    val totalScore: Int,
    val interpretation: String,
    val testTitle: String // <-- Название теста
)

@Dao
interface TestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResultEntity): Long

    @Query("SELECT * FROM test_results WHERE id = :resultId")
    suspend fun getResultById(resultId: Int): TestResultEntity?

    @Query("DELETE FROM test_results WHERE id = :resultId")
    suspend fun deleteResultById(resultId: Int)

    @Query("DELETE FROM test_results")
    suspend fun clearHistory()

    @Query("SELECT * FROM test_results ORDER BY date DESC")
    fun getAllResults(): Flow<List<TestResultEntity>>

    // НОВЫЙ ЗАПРОС: Объединяем результат с названием теста
    @Query("""
        SELECT test_results.id, test_results.testId, test_results.date, 
               test_results.totalScore, test_results.interpretation, 
               tests.title AS testTitle
        FROM test_results
        LEFT JOIN tests ON test_results.testId = tests.id
        ORDER BY test_results.date DESC
    """)
    fun getAllResultsWithTitles(): Flow<List<TestResultWithTitle>>
}