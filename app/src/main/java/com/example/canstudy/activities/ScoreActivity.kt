package com.example.canstudy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityScoreBinding

/**
 * A class that displays the score from the TestActivity. The user can also opt to review the wrong words or restart the test.
 */

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding
    private lateinit var wrongWordList: ArrayList<Int>
    private var score: Int = 0
    private var totalQuestions: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore the values of the following if the activity is destroyed unexpectedly.
        savedInstanceState?.let {
            wrongWordList = it.getIntegerArrayList("wrongWordList") as ArrayList<Int>
            score = it.getInt("score")
            totalQuestions = it.getInt("totalQuestions")
        } ?: initialiseActivity()

        binding.btnScoreHome.setOnClickListener { startActivity(Intent(this@ScoreActivity, MainActivity::class.java)) }
        binding.btnScoreReview.setOnClickListener {
            val intent = Intent(this@ScoreActivity, ReviewActivity::class.java)
            intent.putIntegerArrayListExtra("wrongWordList", wrongWordList)
            startActivity(intent)
        }
        binding.btnScoreRestart.setOnClickListener {
            val intent = Intent(this@ScoreActivity, GameDifficultyActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * A function that initialises the UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarScore)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.game_toolbar_title)
        }
        binding.toolbarScore.setNavigationOnClickListener { onBackPressed() }

        val intent = intent
        wrongWordList = intent.getIntegerArrayListExtra("wrongWordList") as ArrayList<Int>
        score = intent.getIntExtra("score", 0)
        totalQuestions = intent.getIntExtra("totalQuestions", 0)

        binding.tvResultLeftScore.text = score.toString()
        binding.tvResultRightScore.text = totalQuestions.toString()
    }

    /**
     * A function that takes the user back to the Main Activity. The activity stack is cleared
     * to prevent multiple instances of MainActivity.
     */
    override fun onBackPressed() {
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("wrongWordList", wrongWordList)
        outState.putInt("score", score)
        outState.putInt("totalQuestions", totalQuestions)
    }
}