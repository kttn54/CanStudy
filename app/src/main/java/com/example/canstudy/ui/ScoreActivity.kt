package com.example.canstudy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityScoreBinding

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding

    private lateinit var btnScoreHome: AppCompatButton
    private lateinit var btnScoreReview: AppCompatButton
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
    }

    private fun initialiseActivity() {
        btnScoreHome = binding.btnScoreHome
        btnScoreReview = binding.btnScoreReview
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

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}