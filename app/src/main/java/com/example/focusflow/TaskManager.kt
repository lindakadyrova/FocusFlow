package com.example.focusflow

object TaskManager {
    val tasks = mutableListOf<TaskData>()
}

data class TaskData(
    val name: String,
    val firstSubtask: String,
    val allSubtasks: List<String>,
    val time: String
)