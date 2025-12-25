package com.example.focusflow

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val helpIcon = findViewById<ImageView>(R.id.helpIcon)

        backButton.setOnClickListener {
            finish()
        }

        helpIcon.setOnClickListener {
            showHelpDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setMessage("In order to not feel overwhelmed by bigger tasks, we want you to break it into the smallest task possible.\n" +
                    "Like, open text document or search for one source. How many subtasks you want to create is your choice, but the more the better.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }
}