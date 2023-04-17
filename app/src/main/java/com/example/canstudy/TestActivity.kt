package com.example.canstudy

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.databinding.ActivityTestBinding
import com.example.canstudy.databinding.DialogRestartReviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Array
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var ibTick: ImageButton
    private lateinit var ibCross: ImageButton
    private lateinit var btnCheck: Button
    private lateinit var btnReview: Button
    private lateinit var btnRestart: Button
    private lateinit var tvCantoneseDescription: TextView
    private lateinit var tvCantoneseTranslation: TextView
    private lateinit var tvEnglishDescription: TextView
    private lateinit var tvEnglishTranslation: TextView
    private lateinit var tvLeftScore: TextView
    private lateinit var tvRightScore: TextView
    private var correctScore = 0
    private var totalScore = 0
    private var wordsChosen = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        val dao = (application as CanStudyDatabaseApp).db.wordDao()

        ibTick.setOnClickListener { calculateCorrectAnswer() }
        ibCross.setOnClickListener { calculateWrongAnswer() }
        btnCheck.setOnClickListener { displayCantoneseText() }
        btnRestart.setOnClickListener { restartScore() }
    }

    private fun calculateCorrectAnswer() {
        correctScore++
        totalScore++
        tvLeftScore.text = correctScore.toString()
        tvRightScore.text = totalScore.toString()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE
        getWord()
    }

    private fun calculateWrongAnswer() {
        totalScore++
        tvRightScore.text = totalScore.toString()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE
        getWord()
    }

    private fun displayCantoneseText() {
        if (tvCantoneseDescription.visibility == INVISIBLE) {
            tvCantoneseDescription.visibility = VISIBLE
            tvCantoneseTranslation.visibility = VISIBLE
        } else {
            tvCantoneseDescription.visibility = INVISIBLE
            tvCantoneseTranslation.visibility = INVISIBLE
        }
    }

    private fun initialiseActivity() {
        ibTick = binding.ibTick
        ibCross = binding.ibCross
        btnCheck = binding.btnCheck
        btnReview = binding.btnReview
        btnRestart = binding.btnRestart
        tvCantoneseDescription = binding.tvCantoneseDescription
        tvCantoneseTranslation = binding.tvCantoneseTranslation
        tvEnglishDescription = binding.tvEnglishDescription
        tvEnglishTranslation = binding.tvEnglishTranslation
        tvLeftScore = binding.tvLeftScore
        tvRightScore = binding.tvRightScore

        tvCantoneseTranslation.visibility = INVISIBLE
        tvCantoneseDescription.visibility = INVISIBLE

        getWord()
    }

    private fun getWord() {
        val dao = (application as CanStudyDatabaseApp).db.wordDao()
        getWordList(dao) { wordList ->
            // Handle the case where all words have been chosen
            if (wordsChosen.size == wordList.size) {
                addRestartReviewDialog()
                disableElements()
                tvCantoneseDescription.visibility = VISIBLE
                tvCantoneseTranslation.visibility = VISIBLE
                return@getWordList
            }

            var randomValue: Int
            do {
                randomValue = Random.nextInt(0, wordList.size)
            } while (wordsChosen.contains(randomValue))

            tvEnglishTranslation.text = wordList[randomValue].getEnglishWord()
            tvCantoneseTranslation.text = wordList[randomValue].getCantoneseWord()
            wordsChosen.add(randomValue)
        }
    }

    private fun disableElements() {
        ibTick.isEnabled = false
        ibCross.isEnabled = false
        btnCheck.isEnabled = false
    }

    private fun enableElements() {
        ibTick.isEnabled = true
        ibCross.isEnabled = true
        btnCheck.isEnabled = true
    }

    private fun restartScore() {
        correctScore = 0
        totalScore = 0
        tvLeftScore.text = correctScore.toString()
        tvRightScore.text = totalScore.toString()
        wordsChosen.clear()
        enableElements()
        getWord()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE
    }

    private fun addRestartReviewDialog() {
        val restartReviewDialog = Dialog(this)
        val dialogBinding = DialogRestartReviewBinding.inflate(layoutInflater)
        restartReviewDialog.setContentView(dialogBinding.root)
        restartReviewDialog.setCanceledOnTouchOutside(true)

        // Make dialog match the width of the screen
        val window = restartReviewDialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        dialogBinding.btnRestart.setOnClickListener {
            restartReviewDialog.dismiss()
            restartScore()
        }
        dialogBinding.btnReview.setOnClickListener {

        }

        restartReviewDialog.show()
    }

    private fun getWordList(wordDao: WordDao, callback: (ArrayList<WordModel>) -> Unit) {
        val wordList = ArrayList<WordModel>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordModel(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.CORRECT_STATUS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }

}