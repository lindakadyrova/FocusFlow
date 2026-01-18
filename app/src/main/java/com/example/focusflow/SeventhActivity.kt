package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SeventhActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seventh)

        // Find views
        val estimatedTimeText = findViewById<TextView>(R.id.estimatedTimeText)
        val actualTimeText = findViewById<TextView>(R.id.actualTimeText)
        val backToTasksButton = findViewById<Button>(R.id.backToTasksButton)

        // Header views (if you use the same header layout)
        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)

        // Retrieve the data you passed from SixthActivity
        val estimatedMinutes = intent.getStringExtra("ESTIMATED_MINUTES") ?: "Not set"
        val actualMinutes = intent.getIntExtra("ACTUAL_MINUTES", 55)

        // Display values
        estimatedTimeText.text = estimatedMinutes
        actualTimeText.text = "$actualMinutes minutes"

        // Navigation logic
        backToTasksButton.setOnClickListener {
            // Use CLEAR_TOP to ensure the user can't "back" into a finished task session
            val intent = Intent(this, FifthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        // Maintain the understated background aesthetic by handling system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}