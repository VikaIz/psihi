package com.izm.spokoyno.data.dao

import androidx.room.*
import com.izm.spokoyno.data.local.QuestionOptionEntity

@Dao
interface QuestionOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<QuestionOptionEntity>)

    @Query("SELECT * FROM question_options WHERE questionId = :questionId")
    suspend fun getOptionsForQuestion(questionId: Int): List<QuestionOptionEntity>

    // ДОБАВЛЕНО ДЛЯ CRUD
    @Update
    suspend fun updateOption(option: QuestionOptionEntity)

    @Delete
    suspend fun deleteOption(option: QuestionOptionEntity)

    @Query("DELETE FROM question_options WHERE questionId = :questionId")
    suspend fun deleteOptionsForQuestion(questionId: Int)
}