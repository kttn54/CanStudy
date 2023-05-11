package com.example.canstudy.ui

import android.app.Dialog
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.canstudy.CanStudyApp
import com.example.canstudy.Constants
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityGameBinding
import com.example.canstudy.databinding.DialogExitGameBinding
import org.w3c.dom.Text
import kotlin.random.Random

/**
 * A class that adds the Game functionality, allowing users to test themselves in 60 seconds.
 */

class GameActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding : ActivityGameBinding
    private lateinit var tvDifficultySetting : TextView
    private lateinit var tvCountdown : TextView
    private lateinit var tvGameTime : TextView
    private lateinit var tvGameScore : TextView
    private lateinit var tvGameEnglishDescription : TextView
    private lateinit var tvGameEnglishTranslation : TextView
    private lateinit var tvGameWordID : TextView
    private lateinit var btnEasyDifficulty: Button
    private lateinit var btnMediumDifficulty: Button
    private lateinit var btnHardDifficulty: Button
    private lateinit var llGameScore: LinearLayout
    private lateinit var llOptionA: LinearLayout
    private lateinit var llOptionB: LinearLayout
    private lateinit var llOptionC: LinearLayout
    private lateinit var llOptionD: LinearLayout
    private lateinit var tvGameOptionA: AppCompatButton
    private lateinit var tvGameOptionB: AppCompatButton
    private lateinit var tvGameOptionC: AppCompatButton
    private lateinit var tvGameOptionD: AppCompatButton

    private var gameState = "PRE-GAME"
    private var difficultySetting = "Easy"

    private var countdownTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null
    private var countdownTime = 3
    private var gameTime = 60

    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()

    private lateinit var progressBar: ProgressBar

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
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
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Game"
        }

        binding.toolbarGame.setNavigationOnClickListener { onBackPressed() }

        tvDifficultySetting = binding.tvDifficultySetting
        tvCountdown = binding.tvCountdown
        tvGameTime = binding.tvGameTime
        tvGameScore = binding.tvGameScore
        tvGameEnglishDescription = binding.tvGameEnglishDescription
        tvGameEnglishTranslation = binding.tvGameEnglishTranslation
        tvGameWordID = binding.tvGameWordID
        btnEasyDifficulty = binding.btnEasyDifficulty
        btnMediumDifficulty = binding.btnMediumDifficulty
        btnHardDifficulty = binding.btnHardDifficulty
        llGameScore = binding.llGameScore
        llOptionA = binding.llOptionA
        llOptionB = binding.llOptionB
        llOptionC = binding.llOptionC
        llOptionD = binding.llOptionD
        tvGameOptionA = binding.tvGameOptionA
        tvGameOptionB = binding.tvGameOptionB
        tvGameOptionC = binding.tvGameOptionC
        tvGameOptionD = binding.tvGameOptionD

        progressBar = binding.progressBarGame
        mediaPlayer = MediaPlayer.create(this@GameActivity, R.raw.yummy_dim_sum)
    }

    /**
     * A function that describes how each button should react when they are clicked.
     */
    override fun onClick(view: View?) {
        if (gameState == "PRE-GAME") {
            var button = view as Button

            if (button.text.toString() == "Easy") {
                difficultySetting = Constants.EASY_DIFFICULTY
            } else if (button.text.toString() == "Med") {
                difficultySetting = Constants.MEDIUM_DIFFICULTY
            } else {
                difficultySetting = Constants.HARD_DIFFICULTY
            }
            gameState = "MID-GAME"
            setupGame()
        } else {
            var textView = view as TextView
        }

    }

    /**
     * A function that shows the countdown before the game starts.
     */
    private fun setupGame() {
        tvDifficultySetting.visibility = View.GONE
        btnEasyDifficulty.visibility = View.GONE
        btnMediumDifficulty.visibility = View.GONE
        btnHardDifficulty.visibility = View.GONE
        tvCountdown.visibility = View.VISIBLE

        countdownTimer = object : CountDownTimer((countdownTime * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                tvCountdown.text = countdownTime.toString()
                countdownTime--
            }

            override fun onFinish() {
                tvCountdown.visibility = View.GONE
                startGame()
            }
        }.start()
    }

    /**
     * A function that starts the game once the countdown has finished.
     */
    private fun startGame() {
        //mediaPlayer.start()
        binding.toolbarGame.title = "Game - $difficultySetting"
        progressBar.visibility = View.VISIBLE
        tvGameTime.visibility = View.VISIBLE
        tvGameScore.visibility = View.VISIBLE
        tvGameEnglishDescription.visibility = View.VISIBLE
        tvGameEnglishTranslation.visibility = View.VISIBLE
        llGameScore.visibility = View.VISIBLE
        llOptionA.visibility = View.VISIBLE
        llOptionB.visibility = View.VISIBLE
        llOptionC.visibility = View.VISIBLE
        llOptionD.visibility = View.VISIBLE
        tvGameOptionA.visibility = View.VISIBLE
        tvGameOptionB.visibility = View.VISIBLE
        tvGameOptionC.visibility = View.VISIBLE
        tvGameOptionD.visibility = View.VISIBLE

        progressBar.max = 60
        var currentTime = 60

        getWord()

        gameTimer = object : CountDownTimer((gameTime * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                progressBar.progress = currentTime
                tvGameTime.text = currentTime.toString()
                currentTime--
            }

            override fun onFinish() {
                 //mediaPlayer.stop()
            }
        }.start()
    }

    /**
     * A function that retrieves a randomly selected word from the database that has not yet been tested.
     */
    private fun getWord() {
        val dao = (application as CanStudyApp).db.wordDao()
        getWordList(dao) { wordList ->
            wordList.shuffle()

            var wordID: Int
            var selectedList: ArrayList<Int> = ArrayList()

            // Get four random words
            for (i in 1..4) {
                do {
                    randomIndex = Random.nextInt(0, wordList.size)
                    wordID = wordList[randomIndex].ID
                } while (selectedList.contains(wordID))

                selectedList.add(wordID)

                if (i == 1) {
                    tvGameEnglishTranslation.text = wordList[randomIndex].getEnglishWord()
                }
            }

            selectedList.shuffle()

            // TODO: CHANGE THE DATABASE ID NUMBERS TO START FROM 0
            tvGameOptionA.text = wordList[selectedList[0]].getCantoWord()
            tvGameOptionB.text = wordList[selectedList[1]].getCantoWord()
            tvGameOptionC.text = wordList[selectedList[2]].getCantoWord()
            tvGameOptionD.text = wordList[selectedList[3]].getCantoWord()

            // tvGameWordID.text = wordID.toString()
        }
    }

    /**
     * A function that displays a dialog box if the user wants to exit the Game activity.
     */
    override fun onBackPressed() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogExitGameBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnGameDialogYes.setOnClickListener {
            this@GameActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnGameDialogNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    /**
     * A function that stops the media player once the activity is destroyed to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}