package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel_"

class MainViewModel: ViewModel() {

    var currentIndex = 0
    var points: Int = 0
    var isEnabled = true
    var cheatTokens = 3
    var cheatButton = true

    // Question bank list
    private val questionBank: List<Question> = listOf(
        Question(R.string.question_cochin, true),
        Question(R.string.question_africa, false),
        Question(R.string.question_founder, true),
        Question(R.string.question_ocean, true),
        Question(R.string.question_mosquito, true),
        Question(R.string.question_harvard, false),
        Question(R.string.question_elon, false)
    )

    val answer: Boolean get() = questionBank[currentIndex].answer
    val question: Int get() = questionBank[currentIndex].textResId
    val currentQuestion: Question get() = questionBank[currentIndex]
    val questionBankSize: Int get() = questionBank.size
    val hasCheated: Boolean get() = currentQuestion.cheated

    fun getQuestions(): List<Question> {
        return questionBank
    }

    fun incrementIndex() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun decrementIndex() {
        currentIndex = (questionBank.size + (currentIndex - 1)) % questionBank.size
    }

    fun addPoints(points_: Int = 1) {
        points += points_
    }

    fun hasCheated(cond: Boolean) {
        currentQuestion.cheated = cond
    }

}
