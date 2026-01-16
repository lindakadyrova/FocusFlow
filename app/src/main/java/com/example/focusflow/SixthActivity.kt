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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sixth)

        val backButton = findViewById<TextView>(R.id.backArrow)
        val goToPlannedTasksButton = findViewById<Button>(R.id.goToPlannedTasks)
        val nextTaskButton = findViewById<Button>(R.id.addTask)
        val bigTaskText = findViewById<TextView>(R.id.bigTaskText)
        val smallTaskText = findViewById<TextView>(R.id.smallTaskText)

        sharedPreferences = getSharedPreferences("FocusFlowPrefs", MODE_PRIVATE)

        val taskId = intent.getLongExtra("TASK_ID", -1L)

        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@SixthActivity)

            withContext(Dispatchers.Main) {
                if (taskId != -1L) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val allTasks = database.taskDao().getAllTasks()
                        val task = allTasks.find { it.id.toLong() == taskId }

                        withContext(Dispatchers.Main) {
                            if (task != null) {
                                currentTask = task

                                // Subtasks aus JSON laden
                                val subtaskListType = object : TypeToken<List<String>>() {}.type
                                subtasks = Gson().fromJson(task.subtasksJson, subtaskListType)

                                // Gespeicherten Fortschritt laden für diese Task
                                val savedProgress = sharedPreferences.getInt("progress_${task.id}", 0)
                                currentSubtaskIndex = savedProgress

                                // Ersten Subtask anzeigen
                                updateTaskDisplay()
                                updateButtonText()
                            }
                        }
                    }
                }
            }
        }

        backButton.setOnClickListener {
            saveProgress()
            finish()
        }

        goToPlannedTasksButton.setOnClickListener {
            saveProgress()

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

    private fun updateTaskDisplay() {
        val bigTaskText = findViewById<TextView>(R.id.bigTaskText)
        val smallTaskText = findViewById<TextView>(R.id.smallTaskText)

        bigTaskText.text = "${currentTask.taskName} • ${currentTask.dueDate}"

        if (currentSubtaskIndex < subtasks.size) {
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
                saveProgress()
                updateTaskDisplay()
                updateButtonText()
            }
        } else {
            nextTaskButton.text = "FINISH"
            nextTaskButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getDatabase(this@SixthActivity)
                    val updatedTask = currentTask.copy(isCompleted = true)
                    database.taskDao().updateTask(updatedTask)

                    withContext(Dispatchers.Main) {
                        sharedPreferences.edit().remove("progress_${currentTask.id}").apply()

                        val intent = Intent(this@SixthActivity, FifthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun saveProgress() {
        val editor = sharedPreferences.edit()
        editor.putInt("progress_${currentTask.id}", currentSubtaskIndex)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        saveProgress()
    }
}