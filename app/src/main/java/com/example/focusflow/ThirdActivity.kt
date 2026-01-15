package com.example.focusflow

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThirdActivity : AppCompatActivity() {

    private val subtaskEditTextList = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        // Views finden
        val taskName = intent.getStringExtra("EXTRA_TASK_NAME")
        val dueDate = intent.getStringExtra("EXTRA_DATE")
        val headerLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.header)
        val backButton = headerLayout.findViewById<TextView>(R.id.backArrow)
        val helpIcon = findViewById<ImageView>(R.id.helpIcon)
        val taskContainer = findViewById<LinearLayout>(R.id.taskContainer)
        val addIcon = findViewById<ImageView>(R.id.add_icon)
        val scrollView = findViewById<ScrollView>(R.id.taskScrollView)
        val nextButton = findViewById<Button>(R.id.nextButton)

        // BACK BUTTON
        backButton.setOnClickListener {
            finish()
        }

        //NEXT BUTTON
        nextButton.setOnClickListener {
            val subtasks = ArrayList<String>()
            for (editText in subtaskEditTextList) {
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    subtasks.add(text)
                }
            }
            if (subtasks.isEmpty()) {
                Toast.makeText(this, "Try to break it down into at least one step!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, FourthActivity::class.java)
                // Pass EVERYTHING to the next screen
                intent.putExtra("EXTRA_TASK_NAME", taskName)
                intent.putExtra("EXTRA_DATE", dueDate)
                intent.putStringArrayListExtra("EXTRA_SUBTASKS", subtasks)
                startActivity(intent)
            }
        }

        // HILFE DIALOG
        helpIcon.setOnClickListener {
            showHelpDialog()
        }

        // Standardmäßig ein Start-Feld anzeigen
        addNewTaskField(taskContainer, addIcon)

        // ADD ICON
        addIcon.setOnClickListener {
            addNewTaskField(taskContainer, addIcon)
            // Automatisch nach unten scrollen
            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addNewTaskField(container: LinearLayout, addButton: ImageView) {
        val density = resources.displayMetrics.density

        // Weißer Container für EditText + Trash
        val rowLayout = FrameLayout(this)
        val rowParams = LinearLayout.LayoutParams((300 * density).toInt(), (60 * density).toInt())
        rowParams.topMargin = (12 * density).toInt()
        rowLayout.layoutParams = rowParams
        rowLayout.setBackgroundColor(Color.WHITE)
        rowLayout.elevation = 4f

        // EditText
        val editText = EditText(this)
        val editParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        editText.layoutParams = editParams
        editText.hint = "add small task"
        editText.background = null
        editText.gravity = Gravity.CENTER
        editText.setPadding(0, 0, (40 * density).toInt(), 0)
        editText.setTextColor(Color.parseColor("#727272"))
        subtaskEditTextList.add(editText)

        // Trash Icon
        val deleteIcon = ImageView(this)
        val deleteParams = FrameLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt())
        deleteParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        deleteParams.marginEnd = (12 * density).toInt()
        deleteIcon.layoutParams = deleteParams
        deleteIcon.setImageResource(R.drawable.ic_delete)
        deleteIcon.setOnClickListener {
            container.removeView(rowLayout)
            subtaskEditTextList.remove(editText)
        }

        rowLayout.addView(editText)
        rowLayout.addView(deleteIcon)

        // Immer vor dem Plus-Icon einfügen
        val index = container.indexOfChild(addButton)
        container.addView(rowLayout, index)
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