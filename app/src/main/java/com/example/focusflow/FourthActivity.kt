package com.example.focusflow

import android.os.Bundle
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

        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val spinner: Spinner = findViewById(R.id.dropdown_menu)
        val zeiten = arrayOf("< 30 Minuten", "30 Minuten", "60 Minuten", "90 Minuten", "> 90 Minuten")
        val nextButton = findViewById<Button>(R.id.nextButton)

        backButton.setOnClickListener {
            finish()
        }
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item, 
            zeiten
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?,
                                        position: Int, id: Long) {
                val options = zeiten[position]
                Toast.makeText(this@FourthActivity, "Gewählt: $options", Toast.LENGTH_SHORT).show()
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