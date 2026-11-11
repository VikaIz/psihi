package com.izm.spokoyno.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val instruction: String,
    val interpretationsJson: String = "[]" // <-- Должно быть!
)