package com.example.canstudy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.canstudy.Constants
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityGameDifficultyBinding

class GameDifficultyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameDifficultyBinding
    private lateinit var tvDifficultySetting : TextView
    private lateinit var btnEasyDifficulty: Button
    private lateinit var btnMediumDifficulty: Button
    private lateinit var btnHardDifficulty: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDifficultyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        btnEasyDifficulty.setOnClickListener(this)
        btnMediumDifficulty.setOnClickListener(this)
        btnHardDifficulty.setOnClickListener(this)
    }

    /**
     * A function that initialises the UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarGame)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Game"
        }

        tvDifficultySetting = binding.tvDifficultySetting
        btnEasyDifficulty = binding.btnEasyDifficulty
        btnMediumDifficulty = binding.btnMediumDifficulty
        btnHardDifficulty = binding.btnHardDifficulty

        binding.toolbarGame.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(view: View?) {
        var button = view as Button
        var difficultySetting = "Easy"
            if (button.text.toString() == "Easy") {
                difficultySetting = Constants.EASY_DIFFICULTY
            } else if (button.text.toString() == "Med") {
                difficultySetting = Constants.MEDIUM_DIFFICULTY
            } else {
                difficultySetting = Constants.HARD_DIFFICULTY
            }
        val intent = Intent(this@GameDifficultyActivity, GameActivity::class.java)
        intent.putExtra("difficultySetting", difficultySetting)
        startActivity(intent)
    }
}