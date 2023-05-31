package com.example.canstudy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityScoreBinding

/**
 * A class that displays the score from the TestActivity. The user can also opt to review the wrong words or restart the test.
 */

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding

    private lateinit var btnScoreHome: AppCompatButton
    private lateinit var btnScoreReview: AppCompatButton
    private lateinit var btnScoreRestart: AppCompatButton
    private lateinit var tvResultLeftScore: TextView
    private lateinit var tvResultRightScore: TextView
    private lateinit var wrongWordList: ArrayList<Int>
    private var score: Int = 0
    private var totalQuestions: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        btnScoreHome.setOnClickListener { startActivity(Intent(this@ScoreActivity, MainActivity::class.java)) }
        btnScoreReview.setOnClickListener {
            val intent = Intent(this@ScoreActivity, ReviewActivity::class.java)
            intent.putIntegerArrayListExtra("wrongWordList", wrongWordList)
            startActivity(intent)
        }
        btnScoreRestart.setOnClickListener {
            val intent = Intent(this@ScoreActivity, GameDifficultyActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * A function that initialises the UI components.
     */
    private fun initialiseActivity() {
        btnScoreHome = binding.btnScoreHome
        btnScoreReview = binding.btnScoreReview
        btnScoreRestart = binding.btnScoreRestart
        tvResultLeftScore = binding.tvResultLeftScore
        tvResultRightScore = binding.tvResultRightScore

        setSupportActionBar(binding.toolbarScore)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Game"
        }
        binding.toolbarScore.setNavigationOnClickListener { onBackPressed() }

        val intent = intent
        wrongWordList = intent.getIntegerArrayListExtra("wrongWordList") as ArrayList<Int>
        score = intent.getIntExtra("score", 0)
        totalQuestions = intent.getIntExtra("totalQuestions", 0)

        tvResultLeftScore.text = score.toString()
        tvResultRightScore.text = totalQuestions.toString()
    }

    /**
     * A function that takes the user back to the Main Activity.
     */
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}