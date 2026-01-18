package com.example.focusflow

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {

    private lateinit var editText2: EditText
    private lateinit var taskNameInput: EditText
    private val calendar = Calendar.getInstance()
    private var databaseDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val nextButton = findViewById<Button>(R.id.nextButton)
        taskNameInput = findViewById(R.id.editText)
        editText2 = findViewById(R.id.editText2)

        // Kalender-Dialog öffnen
        editText2.setOnClickListener {
            showDatePicker()
        }

        backButton.setOnClickListener {
            finish()
        }

        nextButton.setOnClickListener {
            val taskName = taskNameInput.text.toString()
            if (taskName.isEmpty()) {
                taskNameInput.error = "Please name your task first!"
            }
            if (databaseDate.isEmpty()) {
                editText2.error = "Please put a date!"
            }
            if (taskName.isNotEmpty() && databaseDate.isNotEmpty()) {
                val intent = Intent(this, ThirdActivity::class.java)
                intent.putExtra("EXTRA_TASK_NAME", taskName)
                // Pass the sortable YYYY-MM-DD string to the database
                intent.putExtra("EXTRA_DATE", databaseDate)
                startActivity(intent)
            } else {
                Toast.makeText(this, "The flow requires input!", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, R.style.MyDatePickerTheme, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            updateDateInView()
        }, year, month, day).show()
    }

    private fun updateDateInView() {
        val displayFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)

        val date = calendar.time

        editText2.setText(displayFormat.format(date))
        databaseDate = isoFormat.format(date)
    }
}