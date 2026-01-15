package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button finden
        val addTaskButton = findViewById<Button>(R.id.addTask)

        val plannedTasksButton = findViewById<Button>(R.id.goToPlannedTasks)

        // OnClickListener hinzufügen
        addTaskButton.setOnClickListener {
            // Intent erstellen, um zur SecondActivity zu navigieren
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        plannedTasksButton.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            startActivity(intent)
        }
    }
}