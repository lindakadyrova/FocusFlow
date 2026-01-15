package com.example.focusflow

import android.os.Bundle
import android.content.Intent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val time = arrayOf("< 30 Minutes", "30 Minutes", "60 Minutes", "90 Minutes", "> 90 Minutes")
        val addTaskButton = findViewById<Button>(R.id.addtoTasks)

        backButton.setOnClickListener {
            finish()
        }

        addTaskButton.setOnClickListener {
            val selectedTime = spinner.selectedItem.toString()
            val newTask = TaskData(
                name = taskName ?: "New Task",
                firstSubtask = subtasks?.get(0) ?: "No subtasks",
                allSubtasks = subtasks ?: arrayListOf(),
                time = selectedTime
            )

            TaskManager.tasks.add(newTask)

            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }


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