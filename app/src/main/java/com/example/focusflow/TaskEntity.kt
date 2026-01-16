package com.example.focusflow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val subtasksJson: String,
    val time: String,
    val dueDate: String,
    val isCompleted: Boolean = false
)