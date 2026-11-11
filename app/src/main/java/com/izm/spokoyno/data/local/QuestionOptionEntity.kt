package com.izm.spokoyno.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "question_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questionId"])]
)
data class QuestionOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionId: Int,
    val text: String,
    val score: Int
)