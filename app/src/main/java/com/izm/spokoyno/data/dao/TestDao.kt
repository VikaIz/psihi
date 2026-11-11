package com.izm.spokoyno.data.dao

import androidx.room.*
import com.izm.spokoyno.data.local.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<TestEntity>)

    @Update
    suspend fun updateTest(test: TestEntity)

    @Query("DELETE FROM tests")
    suspend fun deleteAllTests()

    @Query("SELECT * FROM tests")
    fun getAllTests(): Flow<List<TestEntity>>

    // ДОБАВЛЕНО: получение одного теста по ID
    @Query("SELECT * FROM tests WHERE id = :testId")
    suspend fun getTestById(testId: Int): TestEntity?
}