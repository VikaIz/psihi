package com.izm.spokoyno.data.dao

import androidx.room.*
import com.izm.spokoyno.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>
}