package com.example.canstudy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.canstudy.Constants
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityGameDifficultyBinding

/**
 * A class that sets the game difficulty and game time.
 */

class GameDifficultyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameDifficultyBinding
    private lateinit var tvDifficultySetting : TextView
    private lateinit var tvGameTime : TextView
    private lateinit var btnEasyDifficulty: Button
    private lateinit var btnMediumDifficulty: Button
    private lateinit var btnHardDifficulty: Button
    private lateinit var btnFifteenSeconds: Button
    private lateinit var btnThirtySeconds: Button
    private lateinit var btnSixtySeconds: Button
    private lateinit var llDifficultyButtons: LinearLayout
    private lateinit var llGameTime: LinearLayout

    private var gameTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDifficultyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        btnEasyDifficulty.setOnClickListener(this)
        btnMediumDifficulty.setOnClickListener(this)
        btnHardDifficulty.setOnClickListener(this)
        btnFifteenSeconds.setOnClickListener(this)
        btnThirtySeconds.setOnClickListener(this)
        btnSixtySeconds.setOnClickListener(this)
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
        tvGameTime = binding.tvGameTime
        btnEasyDifficulty = binding.btnEasyDifficulty
        btnMediumDifficulty = binding.btnMediumDifficulty
        btnHardDifficulty = binding.btnHardDifficulty
        btnFifteenSeconds = binding.btnFifteenSeconds
        btnThirtySeconds = binding.btnThirtySeconds
        btnSixtySeconds = binding.btnSixtySeconds
        llDifficultyButtons = binding.llDifficultyButtons
        llGameTime = binding.llTimeButtons

        binding.toolbarGame.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function that allocates the game difficulty and time depending on which button is pressed.
     */
    override fun onClick(view: View?) {
        var button = view as Button
        var difficultySetting = "Easy"

        if (button.text.toString() == "15 seconds" || button.text.toString() == "30 seconds" || button.text.toString() == "60 seconds") {
            if (button.text.toString() == "15 seconds") {
                gameTime = 15
            } else if (button.text.toString() == "30 seconds") {
                gameTime = 30
            } else {
                gameTime = 60
            }

            tvDifficultySetting.visibility = View.VISIBLE
            llDifficultyButtons.visibility = View.VISIBLE
            llGameTime.visibility = View.GONE
            tvGameTime.visibility = View.GONE
        }

        if (button.text.toString() == "Easy" || button.text.toString() == "Med" || button.text.toString() == "Hard") {
            if (button.text.toString() == "Easy") {
                difficultySetting = Constants.EASY_DIFFICULTY
            } else if (button.text.toString() == "Med") {
                difficultySetting = Constants.MEDIUM_DIFFICULTY
            } else {
                difficultySetting = Constants.HARD_DIFFICULTY
            }

            val intent = Intent(this@GameDifficultyActivity, GameActivity::class.java)
            intent.putExtra("difficultySetting", difficultySetting)
            intent.putExtra("gameTime", gameTime)
            startActivity(intent)

            tvDifficultySetting.visibility = View.GONE
            llDifficultyButtons.visibility = View.GONE
            llGameTime.visibility = View.VISIBLE
            tvGameTime.visibility = View.VISIBLE
        }
    }

    /**
     * A function that takes the user back to the Game Time buttons if they are shown the Difficulty buttons, else go back to the previous activity.
     */
    override fun onBackPressed() {
        if (llDifficultyButtons.visibility == View.VISIBLE) {
            tvDifficultySetting.visibility = View.GONE
            llDifficultyButtons.visibility = View.GONE
            llGameTime.visibility = View.VISIBLE
            tvGameTime.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }

    }
}