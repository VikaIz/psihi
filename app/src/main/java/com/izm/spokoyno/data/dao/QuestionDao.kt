package com.izm.spokoyno.data.dao

import androidx.room.*
import com.izm.spokoyno.data.local.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>): List<Long>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY orderNumber ASC")
    suspend fun getQuestionsForTest(testId: Int): List<QuestionEntity>

    // ДОБАВЛЕНО ДЛЯ CRUD
    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE testId = :testId")
    suspend fun deleteQuestionsForTest(testId: Int)
}