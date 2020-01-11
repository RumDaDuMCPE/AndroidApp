package com.example.myapplication

import androidx.lifecycle.ViewModel

class CheatViewModel : ViewModel() {
    var isCheater : Boolean = false
    var answerText: Int = R.id.answer_text_view

    fun getAnswerText(answerIsTrue: Boolean) : Int {
        answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }
        return answerText
    }
}