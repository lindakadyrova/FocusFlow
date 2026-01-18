package com.example.focusflow

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FifthActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fifth)

        val headerLayout =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)

        val container = findViewById<LinearLayout>(R.id.finalTaskContainer)
        val addMoreButton = findViewById<android.widget.ImageView>(R.id.addMoreTasks)

        sharedPreferences = getSharedPreferences("FocusFlowPrefs", MODE_PRIVATE)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@FifthActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(this@FifthActivity)
            val savedTasks = database.taskDao().getAllTasks()

            withContext(Dispatchers.Main) {
                for (i in container.childCount - 1 downTo 0) {
                    val child = container.getChildAt(i)
                    if (child != addMoreButton) {
                        container.removeView(child)
                    }
                }

                savedTasks.forEach { task ->
                    val rowLayout = LinearLayout(this@FifthActivity)
                    rowLayout.orientation = LinearLayout.HORIZONTAL
                    rowLayout.gravity = android.view.Gravity.CENTER_VERTICAL
                    rowLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    rowLayout.setPadding(0, 10, 0, 10)

                    val textLayout = LinearLayout(this@FifthActivity)
                    textLayout.orientation = LinearLayout.VERTICAL
                    val textParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    textLayout.layoutParams = textParams

                    val bigTaskLabel = TextView(this@FifthActivity)
                    bigTaskLabel.text = "${task.taskName} • ${task.dueDate}"
                    bigTaskLabel.textSize = 14f
                    bigTaskLabel.setTextColor(Color.parseColor("#9E9E9E"))

                    if (task.isCompleted) {
                        bigTaskLabel.paintFlags =
                            bigTaskLabel.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                        bigTaskLabel.alpha = 0.5f
                    }

                    val subtaskListType = object : TypeToken<List<String>>() {}.type
                    val subtasks: List<String> = Gson().fromJson(task.subtasksJson, subtaskListType)

                    val prefsKey = "progress_${task.id}"

                    val currentSubtaskIndex = if (sharedPreferences.contains(prefsKey)) {
                        val saved = sharedPreferences.getInt(prefsKey, 0)
                        if (saved < subtasks.size && saved >= 0) saved else 0
                    } else {
                        0
                    }

                    val smallTaskLabel = TextView(this@FifthActivity)

                    if (subtasks.isNotEmpty()) {
                        smallTaskLabel.text = subtasks.getOrElse(currentSubtaskIndex) { subtasks[0] }
                    } else {
                        smallTaskLabel.text = "No subtasks"
                    }

                    smallTaskLabel.textSize = 22f
                    smallTaskLabel.setTextColor(Color.parseColor("#424242"))
                    smallTaskLabel.setTypeface(null, Typeface.BOLD)
                    smallTaskLabel.setPadding(0, 0, 0, 10)

                    if (task.isCompleted) {
                        smallTaskLabel.paintFlags =
                            smallTaskLabel.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                        smallTaskLabel.alpha = 0.5f
                    }

                    textLayout.addView(bigTaskLabel)
                    textLayout.addView(smallTaskLabel)

                    val playBtn = android.widget.ImageView(this@FifthActivity)
                    playBtn.setImageResource(R.drawable.ic_play)
                    playBtn.setBackgroundResource(R.drawable.circle_bg)
                    playBtn.setColorFilter(Color.WHITE)

                    val density = resources.displayMetrics.density
                    val size = (45 * density).toInt()
                    playBtn.layoutParams = LinearLayout.LayoutParams(size, size)
                    playBtn.setPadding(12, 12, 12, 12)

                    if (task.isCompleted) {
                        playBtn.alpha = 0.5f
                        playBtn.isEnabled = false
                        playBtn.isClickable = false
                    } else {
                        playBtn.setOnClickListener {
                            val intent = Intent(this@FifthActivity, SixthActivity::class.java)
                            intent.putExtra("TASK_ID", task.id.toLong())
                            startActivity(intent)
                        }
                    }

                    rowLayout.addView(textLayout)
                    rowLayout.addView(playBtn)

                    val index = container.indexOfChild(addMoreButton)
                    container.addView(rowLayout, index)

                    val lineIndex = container.indexOfChild(addMoreButton)

                    val line = View(this@FifthActivity)
                    line.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2
                    )
                    line.setBackgroundColor(Color.parseColor("#BDBDBD"))

                    container.addView(line, lineIndex)
                }
            }
        }

        addMoreButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}