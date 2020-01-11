package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

private const val TAG = "MainActivity_"
private const val KEY_INDEX = "index"
private const val KEY_POINTS = "points"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    // Buttons
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button

    // Questions
    private lateinit var questionTextView: TextView

    private lateinit var tokensTextView: TextView

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var ansButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val points = savedInstanceState?.getInt(KEY_POINTS, 0) ?: 0
        viewModel.points = points
        viewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)

        questionTextView = findViewById(R.id.question_text_view)
        tokensTextView = findViewById(R.id.tokens_text_view)

        val viewUpdates = listOf(nextButton, questionTextView)
        ansButtons = listOf(trueButton, falseButton)


        trueButton.setOnClickListener { checkAnswer(true) }

        falseButton.setOnClickListener { checkAnswer(false) }

        cheatButton.setOnClickListener {
            val answerIsTrue: Boolean = viewModel.answer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        prevButton.setOnClickListener {
            if (viewModel.currentIndex != 0) {
                viewModel.decrementIndex()
                updateQuestion()
            }
        }

        for (key in viewUpdates) {
            key.setOnClickListener {
                if (viewModel.currentIndex == (viewModel.questionBankSize - 1)) finish_()
                viewModel.incrementIndex()
                updateQuestion()
            }
        }
        questionTextView.setText(viewModel.question)
        toggleAnswerButtons(viewModel.isEnabled)

        updateTokenText()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == REQUEST_CODE_CHEAT) {
            val cheatResult = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            viewModel.hasCheated(cheatResult)
        }
        if (viewModel.hasCheated) {
            viewModel.cheatTokens -= 1
            toggleCheatButton()
        }
        updateTokenText()
    }

    fun updateTokenText() {
        tokens_text_view.text = String.format(getString(R.string.tokens_left_text), viewModel.cheatTokens)
    }

    fun calculatePercentageFromPoints(): String {
        val percent = ((viewModel.points.toDouble() / viewModel.questionBankSize) * 100).roundToInt()
        return "$percent%"
    }

    fun toggleAnswerButtons(state: Boolean) {
        for (button in ansButtons) {
            viewModel.isEnabled = state
            button.isEnabled = viewModel.isEnabled
        }
    }

    fun toggleCheatButton() {
        val state = when {
            (viewModel.cheatTokens < 1) || (viewModel.hasCheated) -> false
            else -> true
        }
        viewModel.cheatButton = state
        cheatButton.isEnabled = state
    }

    fun finish_() {
        for (question in viewModel.getQuestions()) {
            question.answered = false
            question.cheated = false
        }
        val percent = calculatePercentageFromPoints()
        Toast.makeText(this, percent, Toast.LENGTH_SHORT).show()
        toggleAnswerButtons(true)
        viewModel.points = 0
        viewModel.cheatTokens = 3
        updateTokenText()
    }

    fun updateQuestion() {
        questionTextView.setText(viewModel.question)
        toggleAnswerButtons(!viewModel.currentQuestion.answered)
        toggleCheatButton()
        updateTokenText()
    }

    fun checkAnswer(userAnswer: Boolean) {
        viewModel.currentQuestion.answered = true
        toggleAnswerButtons(false)
        val correctAnswer = viewModel.answer
        val toast: Int
        val isAnswerCorrect = userAnswer == correctAnswer
        when {
            viewModel.hasCheated && isAnswerCorrect -> toast = R.string.judgement_toast
            viewModel.hasCheated && !isAnswerCorrect -> {
                toast = R.string.stupid_toast
                viewModel.addPoints(-1)
            }
            userAnswer == correctAnswer -> {
                toast = R.string.correct_toast
                viewModel.addPoints(1)
            }
            else -> toast = R.string.incorrect_toast
        }
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, viewModel.currentIndex)
        savedInstanceState.putInt(KEY_POINTS, viewModel.points)
    }


}