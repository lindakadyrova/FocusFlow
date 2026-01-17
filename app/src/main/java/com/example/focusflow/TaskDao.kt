package com.example.focusflow

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)
}