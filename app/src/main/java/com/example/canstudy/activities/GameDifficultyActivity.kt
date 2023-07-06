package com.example.canstudy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.canstudy.Constants
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityGameDifficultyBinding

/**
 * A class that sets the game difficulty and game time.
 */

class GameDifficultyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameDifficultyBinding

    private var gameTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDifficultyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        binding.apply {
            btnEasyDifficulty.setOnClickListener(this@GameDifficultyActivity)
            btnMediumDifficulty.setOnClickListener(this@GameDifficultyActivity)
            btnHardDifficulty.setOnClickListener(this@GameDifficultyActivity)
            btnFifteenSeconds.setOnClickListener(this@GameDifficultyActivity)
            btnThirtySeconds.setOnClickListener(this@GameDifficultyActivity)
            btnSixtySeconds.setOnClickListener(this@GameDifficultyActivity)
        }
    }

    /**
     * A function that initialises the UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.game_toolbar_title)
        }

        binding.toolbarGame.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function that allocates the game difficulty and time depending on which button is pressed.
     */
    override fun onClick(view: View?) {
        var button = view as Button
        var difficultySetting = ""

        val buttonText = button.text.toString()

        if (button.text.toString() == getString(R.string.game_time_15_seconds)
            || button.text.toString() == getString(R.string.game_time_30_seconds)
            || button.text.toString() == getString(R.string.game_time_60_seconds)) {
            if (button.text.toString() == getString(R.string.game_time_15_seconds)) {
                gameTime = 15
            } else if (button.text.toString() == getString(R.string.game_time_15_seconds)) {
                gameTime = 30
            } else {
                gameTime = 60
            }
        }

        binding.apply {
            tvDifficultySetting.visibility = View.VISIBLE
            llDifficultyButtons.visibility = View.VISIBLE
            llGameTime.visibility = View.GONE
            tvGameTime.visibility = View.GONE
        }

        difficultySetting = when (buttonText) {
            "Easy" -> Constants.EASY_DIFFICULTY
            "Med" -> Constants.MEDIUM_DIFFICULTY
            "Hard" -> Constants.HARD_DIFFICULTY
            else -> return
        }

        val intent = Intent(this@GameDifficultyActivity, GameActivity::class.java)
        intent.putExtra("difficultySetting", difficultySetting)
        intent.putExtra("gameTime", gameTime)
        startActivity(intent)

        binding.apply {
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
        if (binding.llDifficultyButtons.visibility == View.VISIBLE) {
            binding.apply {
                tvDifficultySetting.visibility = View.GONE
                llDifficultyButtons.visibility = View.GONE
                llGameTime.visibility = View.VISIBLE
                tvGameTime.visibility = View.VISIBLE
            }
        } else {
            super.onBackPressed()
        }

    }
}