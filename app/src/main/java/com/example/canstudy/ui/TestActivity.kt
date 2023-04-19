package com.example.canstudy.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityTestBinding
import com.example.canstudy.databinding.DialogRestartReviewBinding
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    private lateinit var tvWordID: TextView
    private lateinit var tvLeftScore: TextView
    private lateinit var tvRightScore: TextView
    private var correctScore = 0
    private var totalScore = 0
    private var repeatedWords = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        ibTick.setOnClickListener { calculateCorrectAnswer() }
        ibCross.setOnClickListener { calculateWrongAnswer() }
        btnCheck.setOnClickListener { displayCantoneseText() }
        btnRestart.setOnClickListener { restartScore() }
        btnReview.setOnClickListener { goToReviewActivity() }
    }

    private fun initialiseActivity() {
        setSupportActionBar(binding?.toolbarTestActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Test"
        }
        binding.toolbarTestActivity?.setNavigationOnClickListener { onBackPressed() }

        ibTick = binding.ibTick
        ibCross = binding.ibCross
        btnCheck = binding.btnCheck
        btnReview = binding.btnReview
        btnRestart = binding.btnRestart
        tvCantoneseDescription = binding.tvCantoneseDescription
        tvCantoneseTranslation = binding.tvCantoneseTranslation
        tvEnglishDescription = binding.tvEnglishDescription
        tvEnglishTranslation = binding.tvEnglishTranslation
        tvWordID = binding.tvWordID
        tvLeftScore = binding.tvLeftScore
        tvRightScore = binding.tvRightScore

        tvCantoneseTranslation.visibility = INVISIBLE
        tvCantoneseDescription.visibility = INVISIBLE

        getWord()
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

        val tvWrongWordID = tvWordID.text.toString()
        val IDNumber = tvWrongWordID.toInt()
        wrongWordList.add(IDNumber)

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

    private fun goToReviewActivity() {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putIntegerArrayListExtra("key", wrongWordList)
        startActivity(intent)
    }

    private fun getWord() {
        val dao = (application as CanStudyApp).db.wordDao()
        getWordList(dao) { wordList ->
            // Handle the case where all words have been chosen
            if (repeatedWords.size == wordList.size) {
                tvCantoneseDescription.visibility = VISIBLE
                tvCantoneseTranslation.visibility = VISIBLE
                addRestartReviewDialog()
                disableElements()
                repeatedWords.clear()
                return@getWordList
            }

            wordList.shuffle()

            do {
                randomIndex = Random.nextInt(0, wordList.size)
            } while (repeatedWords.contains(randomIndex))

            tvEnglishTranslation.text = wordList[randomIndex].getEnglishWord()
            tvCantoneseTranslation.text = wordList[randomIndex].getCantoWord()
            tvWordID.text = wordList[randomIndex].getId().toString()
            repeatedWords.add(randomIndex)
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
        repeatedWords.clear()
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
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putIntegerArrayListExtra("key", wrongWordList)
            startActivity(intent)
        }

        restartReviewDialog.show()
    }

    private fun getWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordEntity(
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

    override fun onDestroy() {
        super.onDestroy()
        (application as CanStudyApp).db.close()
        repeatedWords.clear()
    }
}