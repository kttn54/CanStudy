package com.example.canstudy.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityTestBinding
import com.example.canstudy.databinding.DialogRestartReviewBinding
import kotlin.random.Random

/**
 * A class that tests the User English-Cantonese words based on the latest database installed.
 */

class TestActivity : BaseActivity() {

    private lateinit var binding: ActivityTestBinding
    private var correctScore = 0
    private var totalScore = 0
    private var repeatedWordList = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()
    private var englishWord = ""
    private var cantoneseWord = ""
    private var isWordVisible = false

    companion object {
        private val CORRECT_SCORE = "CORRECT_SCORE"
        private val TOTAL_SCORE = "TOTAL_SCORE"
        private val ENGLISH_WORD = "ENGLISH_WORD"
        private val CANTONESE_WORD = "CANTONESE_WORD"
        private val IS_WORD_VISIBLE = "IS_WORD_VISIBLE"
        private val WRONG_WORDS = "WRONG_WORDS"
        private val REPEATED_WORDS = "REPEATED_WORDS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            correctScore = savedInstanceState.getInt(CORRECT_SCORE)
            binding.tvTestLeftScore.text = correctScore.toString()
            totalScore = savedInstanceState.getInt(TOTAL_SCORE)
            binding.tvTestRightScore.text = totalScore.toString()

            englishWord = savedInstanceState.getString(ENGLISH_WORD).toString()
            binding.tvTestEnglishTranslation.text = englishWord

            cantoneseWord = savedInstanceState.getString(CANTONESE_WORD).toString()
            binding.tvTestCantoneseTranslation.text = cantoneseWord

            isWordVisible = savedInstanceState.getBoolean(IS_WORD_VISIBLE)
            if (isWordVisible) {
                binding.tvTestCantoneseDescription.visibility = VISIBLE
                binding.tvTestCantoneseTranslation.visibility = VISIBLE
            } else {
                binding.tvTestCantoneseDescription.visibility = INVISIBLE
                binding.tvTestCantoneseTranslation.visibility = INVISIBLE
            }

            repeatedWordList = savedInstanceState.getIntegerArrayList(REPEATED_WORDS) as ArrayList<Int>
            wrongWordList = savedInstanceState.getIntegerArrayList(WRONG_WORDS) as ArrayList<Int>
        } else {
            initialiseActivity()
        }

        binding.apply {
            ibTick.setOnClickListener { calculateCorrectAnswer() }
            ibCross.setOnClickListener { calculateWrongAnswer() }
            btnCheck.setOnClickListener { displayCantoneseText() }
            btnRestart.setOnClickListener { restartScore() }
            btnReview.setOnClickListener { goToReviewActivity() }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(CORRECT_SCORE, correctScore)
        outState.putInt(TOTAL_SCORE, totalScore)
        outState.putString(ENGLISH_WORD, englishWord)
        outState.putString(CANTONESE_WORD, cantoneseWord)
        outState.putBoolean(IS_WORD_VISIBLE, isWordVisible)
        outState.putIntegerArrayList(REPEATED_WORDS, repeatedWordList)
        outState.putIntegerArrayList(WRONG_WORDS, wrongWordList)
    }

    /**
     * A function that initialises the word list and UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarTestActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Test"
        }
        binding.toolbarTestActivity.setNavigationOnClickListener { onBackPressed() }

        binding.tvTestCantoneseTranslation.visibility = INVISIBLE
        binding.tvTestCantoneseDescription.visibility = INVISIBLE

        getWord()
    }

    /**
     * A function that describes what happens when the user inputs a correct answer.
     */
    private fun calculateCorrectAnswer() {
        correctScore++
        totalScore++
        binding.tvTestLeftScore.text = correctScore.toString()
        binding.tvTestRightScore.text = totalScore.toString()
        binding.tvTestCantoneseDescription.visibility = INVISIBLE
        binding.tvTestCantoneseTranslation.visibility = INVISIBLE
        getWord()
    }

    /**
     * A function that describes what happens when the user inputs a wrong answer.
     */
    private fun calculateWrongAnswer() {
        totalScore++
        binding.tvTestRightScore.text = totalScore.toString()
        binding.tvTestCantoneseDescription.visibility = INVISIBLE
        binding.tvTestCantoneseTranslation.visibility = INVISIBLE

        val tvWrongWordID = binding.tvTestWordID.text.toString()
        val IDNumber = tvWrongWordID.toInt()
        wrongWordList.add(IDNumber)

        getWord()
    }

    /**
     * A function that displays Cantonese text which is initially invisible.
     */
    private fun displayCantoneseText() {
        if (binding.tvTestCantoneseDescription.visibility == INVISIBLE) {
            binding.tvTestCantoneseDescription.visibility = VISIBLE
            binding.tvTestCantoneseTranslation.visibility = VISIBLE
            isWordVisible = true
        } else {
            binding.tvTestCantoneseDescription.visibility = INVISIBLE
            binding.tvTestCantoneseTranslation.visibility = INVISIBLE
            isWordVisible = false
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
            if (repeatedWordList.size == wordList.size) {
                binding.tvTestCantoneseDescription.visibility = VISIBLE
                binding.tvTestCantoneseTranslation.visibility = VISIBLE
                addRestartReviewDialog()
                disableElements()
                repeatedWordList.clear()
                return@getAllWordList
            }
            wordList.shuffle()

            var wordID: Int

            do {
                randomIndex = Random.nextInt(0, wordList.size)
                wordID = wordList[randomIndex].ID
            } while (repeatedWordList.contains(wordID))

            englishWord = wordList[randomIndex].getEnglishWord()
            binding.tvTestEnglishTranslation.text = englishWord

            cantoneseWord = wordList[randomIndex].getCantoWord()
            binding.tvTestCantoneseTranslation.text = cantoneseWord

            binding.tvTestWordID.text = wordID.toString()
            repeatedWordList.add(wordID)
        }
    }

    /**
     * A function that disables certain UI elements.
     */
    private fun disableElements() {
        binding.apply {
            ibTick.isEnabled = false
            ibCross.isEnabled = false
            btnCheck.isEnabled = false
        }
    }

    /**
     * A function that enables certain UI elements.
     */
    private fun enableElements() {
        binding.apply {
            ibTick.isEnabled = true
            ibCross.isEnabled = true
            btnCheck.isEnabled = true
        }
    }

    /**
     * A function that restarts the score and resets the UI components.
     */
    private fun restartScore() {
        correctScore = 0
        totalScore = 0
        binding.tvTestLeftScore.text = correctScore.toString()
        binding.tvTestRightScore.text = totalScore.toString()
        repeatedWordList.clear()
        wrongWordList.clear()
        enableElements()
        getWord()
        binding.tvTestCantoneseDescription.visibility = VISIBLE
        binding.tvTestCantoneseTranslation.visibility = VISIBLE
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
        repeatedWordList.clear()
    }
}