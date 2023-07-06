package com.example.canstudy.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityTestBinding
import com.example.canstudy.databinding.DialogRestartReviewBinding
import kotlin.random.Random

/**
 * A class that tests the User English-Cantonese words based on the latest database installed.
 */

class TestActivity : BaseActivity() {

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
    private lateinit var tvTestWordID: TextView
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

    /**
     * A function that initialises the word list and UI components.
     */
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
        tvCantoneseDescription = binding.tvTestCantoneseDescription
        tvCantoneseTranslation = binding.tvTestCantoneseTranslation
        tvEnglishDescription = binding.tvTestEnglishDescription
        tvEnglishTranslation = binding.tvTestEnglishTranslation
        tvTestWordID = binding.tvTestWordID
        tvLeftScore = binding.tvTestLeftScore
        tvRightScore = binding.tvTestRightScore

        tvCantoneseTranslation.visibility = INVISIBLE
        tvCantoneseDescription.visibility = INVISIBLE

        getWord()
    }

    /**
     * A function that describes what happens when the user inputs a correct answer.
     */
    private fun calculateCorrectAnswer() {
        correctScore++
        totalScore++
        tvLeftScore.text = correctScore.toString()
        tvRightScore.text = totalScore.toString()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE
        getWord()
    }

    /**
     * A function that describes what happens when the user inputs a wrong answer.
     */
    private fun calculateWrongAnswer() {
        totalScore++
        tvRightScore.text = totalScore.toString()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE

        val tvWrongWordID = tvTestWordID.text.toString()
        val IDNumber = tvWrongWordID.toInt()
        wrongWordList.add(IDNumber)

        getWord()
    }

    /**
     * A function that displays Cantonese text which is initially invisible.
     */
    private fun displayCantoneseText() {
        if (tvCantoneseDescription.visibility == INVISIBLE) {
            tvCantoneseDescription.visibility = VISIBLE
            tvCantoneseTranslation.visibility = VISIBLE
        } else {
            tvCantoneseDescription.visibility = INVISIBLE
            tvCantoneseTranslation.visibility = INVISIBLE
        }
    }

    /**
     * A function that takes the user to the Review activity.
     */
    private fun goToReviewActivity() {
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putIntegerArrayListExtra("wrongWordList", wrongWordList)
        startActivity(intent)
    }

    /**
     * A function that retrieves a randomly selected word from the database that has not yet been tested.
     */
    private fun getWord() {
        val dao = (application as CanStudyApp).db.wordDao()
        getAllWordList(dao) { wordList ->
            // Handle the case where all words have been chosen
            if (repeatedWords.size == wordList.size) {
                tvCantoneseDescription.visibility = VISIBLE
                tvCantoneseTranslation.visibility = VISIBLE
                addRestartReviewDialog()
                disableElements()
                repeatedWords.clear()
                return@getAllWordList
            }
            wordList.shuffle()

            var wordID: Int

            do {
                randomIndex = Random.nextInt(0, wordList.size)
                wordID = wordList[randomIndex].ID
            } while (repeatedWords.contains(wordID))

            tvEnglishTranslation.text = wordList[randomIndex].getEnglishWord()
            tvCantoneseTranslation.text = wordList[randomIndex].getCantoWord()
            tvTestWordID.text = wordID.toString()
            repeatedWords.add(wordID)
        }
    }

    /**
     * A function that disables certain UI elements.
     */
    private fun disableElements() {
        ibTick.isEnabled = false
        ibCross.isEnabled = false
        btnCheck.isEnabled = false
    }

    /**
     * A function that enables certain UI elements.
     */
    private fun enableElements() {
        ibTick.isEnabled = true
        ibCross.isEnabled = true
        btnCheck.isEnabled = true
    }

    /**
     * A function that restarts the score and resets the UI components.
     */
    private fun restartScore() {
        correctScore = 0
        totalScore = 0
        tvLeftScore.text = correctScore.toString()
        tvRightScore.text = totalScore.toString()
        repeatedWords.clear()
        wrongWordList.clear()
        enableElements()
        getWord()
        tvCantoneseDescription.visibility = INVISIBLE
        tvCantoneseTranslation.visibility = INVISIBLE
    }

    /**
     * A function that creates the dialog with the option of restarting or going to the review activity
     * once the user has finished testing with all the available words.
     */
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
            intent.putIntegerArrayListExtra("wrongWordList", wrongWordList)
            startActivity(intent)
        }

        restartReviewDialog.show()
    }

    /**
     * A function that empties the repeated words array if the activity is finished.
     */
    override fun onDestroy() {
        super.onDestroy()
        (application as CanStudyApp).db.close()
        repeatedWords.clear()
    }
}