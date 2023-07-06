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
    private var repeatedWords = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        binding.apply {
            ibTick.setOnClickListener { calculateCorrectAnswer() }
            ibCross.setOnClickListener { calculateWrongAnswer() }
            btnCheck.setOnClickListener { displayCantoneseText() }
            btnRestart.setOnClickListener { restartScore() }
            btnReview.setOnClickListener { goToReviewActivity() }
        }
    }

    /**
     * A function that initialises the word list and UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding?.toolbarTestActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Test"
        }
        binding.toolbarTestActivity?.setNavigationOnClickListener { onBackPressed() }

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
        } else {
            binding.tvTestCantoneseDescription.visibility = INVISIBLE
            binding.tvTestCantoneseTranslation.visibility = INVISIBLE
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
                binding.tvTestCantoneseDescription.visibility = VISIBLE
                binding.tvTestCantoneseTranslation.visibility = VISIBLE
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

            binding.tvTestEnglishTranslation.text = wordList[randomIndex].getEnglishWord()
            binding.tvTestCantoneseTranslation.text = wordList[randomIndex].getCantoWord()
            binding.tvTestWordID.text = wordID.toString()
            repeatedWords.add(wordID)
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
        repeatedWords.clear()
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
        repeatedWords.clear()
    }
}