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

        val estimatedTimeText = findViewById<TextView>(R.id.estimatedTimeText)
        val actualTimeText = findViewById<TextView>(R.id.actualTimeText)
        val backToTasksButton = findViewById<Button>(R.id.backToTasksButton)

        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)

        val estimatedMinutes = intent.getStringExtra("ESTIMATED_MINUTES") ?: "Not set"
        val actualMinutes = intent.getIntExtra("ACTUAL_MINUTES", 0)

        estimatedTimeText.text = estimatedMinutes

        if (actualMinutes < 1) {
            actualTimeText.text = "less than a minute"
        } else {
            actualTimeText.text = "$actualMinutes minutes"
        }

        backToTasksButton.setOnClickListener {
            val intent = Intent(this, FifthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}