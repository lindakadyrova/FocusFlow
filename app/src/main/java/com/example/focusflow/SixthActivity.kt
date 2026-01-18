package com.example.focusflow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SixthActivity : AppCompatActivity() {

    private lateinit var currentTask: TaskEntity
    private lateinit var subtasks: List<String>
    private var currentSubtaskIndex = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var sessionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sixth)

        val backButton = findViewById<TextView>(R.id.backArrow)
        val goToPlannedTasksButton = findViewById<Button>(R.id.goToPlannedTasks)
        val nextTaskButton = findViewById<Button>(R.id.addTask)

        sharedPreferences = getSharedPreferences("FocusFlowPrefs", MODE_PRIVATE)
        val taskId = intent.getLongExtra("TASK_ID", -1L)

        sessionStartTime = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@SixthActivity)
            val allTasks = database.taskDao().getAllTasks()
            val task = allTasks.find { it.id.toLong() == taskId }

            withContext(Dispatchers.Main) {
                if (task != null) {
                    currentTask = task
                    val subtaskListType = object : TypeToken<List<String>>() {}.type
                    subtasks = Gson().fromJson(task.subtasksJson, subtaskListType)

                    val savedProgress = sharedPreferences.getInt("progress_${task.id}", -1)

                    currentSubtaskIndex = if (savedProgress == -1 || savedProgress >= subtasks.size) 0 else savedProgress

                    saveProgress()

                    updateTaskDisplay()
                    updateButtonText()
                }
            }
        }

        backButton.setOnClickListener {
            if (::currentTask.isInitialized) {
                saveCurrentSessionTime()
                saveProgress()
            }
            finish()
        }

        goToPlannedTasksButton.setOnClickListener {
            if (::currentTask.isInitialized) {
                saveCurrentSessionTime()
                saveProgress()
            }
            val intent = Intent(this@SixthActivity, FifthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveCurrentSessionTime() {
        if (::currentTask.isInitialized) {
            val now = System.currentTimeMillis()
            val duration = now - sessionStartTime

            val totalTimeKey = "total_time_${currentTask.id}"
            val previouslySavedTime = sharedPreferences.getLong(totalTimeKey, 0L)

            sharedPreferences.edit()
                .putLong(totalTimeKey, previouslySavedTime + duration)
                .apply()

            sessionStartTime = now
        }
    }

    override fun onPause() {
        super.onPause()
        saveCurrentSessionTime()
    }

    private fun updateTaskDisplay() {
        val bigTaskText = findViewById<TextView>(R.id.bigTaskText)
        val smallTaskText = findViewById<TextView>(R.id.smallTaskText)

        // Add this check to prevent crashes
        if (!::currentTask.isInitialized) return

        bigTaskText.text = currentTask.taskName
        if (subtasks.isNotEmpty() && currentSubtaskIndex < subtasks.size) {
            smallTaskText.text = subtasks[currentSubtaskIndex]
        }
    }

    private fun updateButtonText() {
        val nextTaskButton = findViewById<Button>(R.id.addTask)
        val hasNextSubtask = currentSubtaskIndex < subtasks.size - 1

        if (hasNextSubtask) {
            nextTaskButton.text = "Next Task"
            nextTaskButton.setOnClickListener {
                currentSubtaskIndex++
                updateTaskDisplay()
                saveProgress()
                updateButtonText()
            }
        } else {
            nextTaskButton.text = "FINISH"
            nextTaskButton.setOnClickListener {
                saveCurrentSessionTime()
                lifecycleScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getDatabase(this@SixthActivity)
                    val updatedTask = currentTask.copy(isCompleted = true)
                    database.taskDao().updateTask(updatedTask)

                    withContext(Dispatchers.Main) {
                        val totalMillis = sharedPreferences.getLong("total_time_${currentTask.id}", 0L)
                        val totalMinutes = (totalMillis / 1000) / 60

                        sharedPreferences.edit().remove("progress_${currentTask.id}").apply()
                        sharedPreferences.edit().remove("total_time_${currentTask.id}").apply()

                        val intent = Intent(this@SixthActivity, SeventhActivity::class.java)
                        intent.putExtra("ESTIMATED_MINUTES", currentTask.time)
                        intent.putExtra("ACTUAL_MINUTES", totalMinutes.toInt())
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun saveProgress() {
        if (::currentTask.isInitialized) {
            sharedPreferences.edit()
                .putInt("progress_${currentTask.id}", currentSubtaskIndex)
                .apply()
        }
    }
}