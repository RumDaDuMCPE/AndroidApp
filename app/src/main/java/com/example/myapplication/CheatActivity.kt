package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE = "com.example.myapplication.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.example.myapplication.answer_shown"

private lateinit var answerTextView: TextView
private lateinit var showAnswerButton: Button

class CheatActivity : AppCompatActivity() {

    private val viewModel: CheatViewModel by lazy  {
        ViewModelProviders.of(this).get(CheatViewModel::class.java)
    }


    private var answerIsTrue = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        //answer
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            updateText(answerIsTrue)
        }
        if (viewModel.isCheater) updateText(answerIsTrue)
    }

    private fun updateText(answerIsTrue: Boolean) {
        viewModel.isCheater = true
        answerTextView.setText(viewModel.getAnswerText(answerIsTrue))
        showAnswerButton.isEnabled = !(viewModel.isCheater)
        setAnswerShownResult()
    }

    private fun setAnswerShownResult() {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, viewModel.isCheater)
        }
        setResult(Activity.RESULT_OK, data)
    }


    companion object {
        fun newIntent(context: Context, answerIsTrue: Boolean) : Intent {
            return Intent(context, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}
