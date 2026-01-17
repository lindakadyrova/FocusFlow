package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson

class FourthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fourth)

        val taskName = intent.getStringExtra("EXTRA_TASK_NAME")
        val dueDate = intent.getStringExtra("EXTRA_DATE")
        val subtasks = intent.getStringArrayListExtra("EXTRA_SUBTASKS")
        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val spinner: Spinner = findViewById(R.id.dropdown_menu)
        val addTaskButton = findViewById<Button>(R.id.addtoTasks)
        val startNowButton = findViewById<Button>(R.id.goToPlannedTasks)

        backButton.setOnClickListener {
            finish()
        }

        addTaskButton.setOnClickListener {
            val selectedTime = spinner.selectedItem.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val subtasksJson = Gson().toJson(subtasks)
                val database = AppDatabase.getDatabase(this@FourthActivity)

                val newTask = TaskEntity(
                    taskName = taskName ?: "Untitled Task",
                    subtasksJson = subtasksJson,
                    time = selectedTime,
                    dueDate = dueDate ?: "No Date"
                )

                database.taskDao().insertTask(newTask)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FourthActivity, "Task added to To-Do list!", Toast.LENGTH_SHORT).show()

                    // Zurück zum Hauptbildschirm (FifthActivity)
                    val intent = Intent(this@FourthActivity, FifthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }
        }

        startNowButton.setOnClickListener {
            val selectedTime = spinner.selectedItem.toString()

            if (taskName.isNullOrEmpty()) {
                Toast.makeText(this, "Task name is missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (subtasks.isNullOrEmpty()) {
                Toast.makeText(this, "No subtasks found!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val subtasksJson = Gson().toJson(subtasks)
                val database = AppDatabase.getDatabase(this@FourthActivity)

                val newTask = TaskEntity(
                    taskName = taskName,
                    subtasksJson = subtasksJson,
                    time = selectedTime,
                    dueDate = dueDate ?: "No Date"
                )

                val taskId = database.taskDao().insertTask(newTask)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FourthActivity, "Task saved and starting session!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@FourthActivity, SixthActivity::class.java)
                    intent.putExtra("TASK_ID", taskId)
                    startActivity(intent)
                }
            }
        }

        val time = arrayOf("< 30 Minutes", "30 Minutes", "60 Minutes", "90 Minutes", "> 90 Minutes")
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            time
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?,
                                        position: Int, id: Long) {
                val options = time[position]
                Toast.makeText(this@FourthActivity, "Selected: $options", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}