package com.izm.spokoyno.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val testId: Int,
    val date: Long, // храним время в миллисекундах
    val totalScore: Int,
    val interpretation: String
)