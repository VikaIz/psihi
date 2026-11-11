package com.izm.spokoyno.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.izm.spokoyno.data.dao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        TestEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        TestResultEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun testDao(): TestDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionOptionDao(): QuestionOptionDao
    abstract fun testResultDao(): TestResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spokoyno_database"
                )
                    // ИСПРАВЛЕНО: Добавлен параметр dropAllTables = true
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDatabase(db: AppDatabase) {
            val test1 = TestEntity(1, "Шкала тревожности", "Оцените свое состояние за последнюю неделю", "Тревожность", "Отвечайте честно")
            val test2 = TestEntity(2, "Уровень стресса", "Оцените уровень стресса", "Стресс", "Вспомните последний месяц")

            db.testDao().insertTests(listOf(test1, test2))

            val q1 = QuestionEntity(testId = 1, text = "Я чувствовал тревогу", orderNumber = 1)
            val q2 = QuestionEntity(testId = 1, text = "Мне было трудно расслабиться", orderNumber = 2)
            val questionIds = db.questionDao().insertQuestions(listOf(q1, q2))

            val options = listOf(
                QuestionOptionEntity(questionId = questionIds[0].toInt(), text = "Никогда", score = 0),
                QuestionOptionEntity(questionId = questionIds[0].toInt(), text = "Часто", score = 3),
                QuestionOptionEntity(questionId = questionIds[1].toInt(), text = "Никогда", score = 0),
                QuestionOptionEntity(questionId = questionIds[1].toInt(), text = "Часто", score = 3)
            )
            db.questionOptionDao().insertOptions(options)
        }
    }
}