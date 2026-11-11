package com.izm.spokoyno.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = TestEntity::class,
            parentColumns = ["id"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["testId"])]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: Int,
    val text: String,
    val orderNumber: Int
)