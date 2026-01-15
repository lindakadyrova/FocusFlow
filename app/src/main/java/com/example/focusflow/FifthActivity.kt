package com.example.focusflow

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FifthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fifth)

        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val container = findViewById<LinearLayout>(R.id.finalTaskContainer)

        container.removeAllViews()

        TaskManager.tasks.forEach { task ->

            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.gravity = android.view.Gravity.CENTER_VERTICAL
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.setPadding(0, 10, 0, 10)

            val textLayout = LinearLayout(this)
            textLayout.orientation = LinearLayout.VERTICAL
            val textParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            textLayout.layoutParams = textParams

            // 1. Big Task Label
            val bigTaskLabel = TextView(this)
            bigTaskLabel.text = task.name // Use task from the loop
            bigTaskLabel.textSize = 14f
            bigTaskLabel.setTextColor(Color.parseColor("#9E9E9E"))

            // 2. Small Task Label
            val smallTaskLabel = TextView(this)
            smallTaskLabel.text = task.firstSubtask // Use task from the loop
            smallTaskLabel.textSize = 22f
            smallTaskLabel.setTextColor(Color.parseColor("#424242"))
            smallTaskLabel.setTypeface(null, Typeface.BOLD)
            smallTaskLabel.setPadding(0, 0, 0, 10)

            textLayout.addView(bigTaskLabel)
            textLayout.addView(smallTaskLabel)

            // 3. Play Button
            val playBtn = android.widget.ImageView(this)
            playBtn.setImageResource(R.drawable.ic_play)
            playBtn.setBackgroundResource(R.drawable.circle_bg)
            playBtn.setColorFilter(Color.WHITE)

            val density = resources.displayMetrics.density
            val size = (45 * density).toInt()
            playBtn.layoutParams = LinearLayout.LayoutParams(size, size)
            playBtn.setPadding(10, 10, 10, 10)

            // Assemble
            rowLayout.addView(textLayout)
            rowLayout.addView(playBtn)
            container.addView(rowLayout)

            // 4. Separator Line
            val line = View(this)
            line.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2)
            line.setBackgroundColor(Color.parseColor("#BDBDBD"))
            container.addView(line)
        }

        val addMoreButton = findViewById<android.widget.ImageView>(R.id.addMoreTasks)

        addMoreButton.setOnClickListener {
            // Go back to the beginning of the task input flow
            val intent = Intent(this, SecondActivity::class.java)

            // This flag clears the "stack" so the user doesn't just keep
            // stacking screens on top of each other
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(intent)
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