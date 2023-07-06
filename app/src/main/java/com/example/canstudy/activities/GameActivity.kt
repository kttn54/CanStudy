package com.example.canstudy.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.databinding.ActivityGameBinding
import com.example.canstudy.databinding.DialogExitGameBinding
import com.example.canstudy.db.CanStudyApp
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * A class that adds the Game functionality, allowing users to test themselves in 60 seconds.
 */

class GameActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding : ActivityGameBinding

    private var difficultySetting = "Easy"
    private var countdownTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null
    private var countdownTime = 3
    private var gameTime = 60

    private var score = 0
    private var totalQuestions = 0

    private var wordList: ArrayList<WordEntity> = arrayListOf()
    private var repeatedWordList = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()
    private var correctCantoAnswer: String = "initialised"

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        binding.btnGameOptionA.setOnClickListener(this)
        binding.btnGameOptionB.setOnClickListener(this)
        binding.btnGameOptionC.setOnClickListener(this)
        binding.btnGameOptionD.setOnClickListener(this)
    }

    /**
     * A function that initialises the UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Game"
        }

        binding.toolbarGame.setNavigationOnClickListener { onBackPressed() }

        val intent = intent
        difficultySetting = intent.getStringExtra("difficultySetting").toString()
        gameTime = intent.getIntExtra("gameTime", 0)

        progressBar = binding.progressBarGame

        setupGame()
    }

    /**
     * A function that shows the countdown before the game starts.
     */
    private fun setupGame() {
        binding.tvCountdown.visibility = View.VISIBLE

        countdownTimer = object : CountDownTimer((countdownTime * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                binding.tvCountdown.text = countdownTime.toString()
                countdownTime--
            }

            override fun onFinish() {
                binding.tvCountdown.visibility = View.GONE
                startGame()
            }
        }.start()
    }

    /**
     * A function that starts the game once the countdown has finished.
     */
    private fun startGame() {
        binding.apply {
            toolbarGame.title = "Game - $difficultySetting - $gameTime seconds"
            progressBar.visibility = View.VISIBLE
            tvGameTime.visibility = View.VISIBLE
            tvGameEnglishDescription.visibility = View.VISIBLE
            tvGameEnglishTranslation.visibility = View.VISIBLE
            llOptionA.visibility = View.VISIBLE
            llOptionB.visibility = View.VISIBLE
            llOptionC.visibility = View.VISIBLE
            llOptionD.visibility = View.VISIBLE
            btnGameOptionA.visibility = View.VISIBLE
            btnGameOptionB.visibility = View.VISIBLE
            btnGameOptionC.visibility = View.VISIBLE
            btnGameOptionD.visibility = View.VISIBLE
        }

        progressBar.max = gameTime
        var currentTime = gameTime

        getWord()

        gameTimer = object : CountDownTimer((gameTime * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                progressBar.progress = currentTime
                binding.tvGameTime.text = currentTime.toString()
                currentTime--
            }

            override fun onFinish() {
                val intent = Intent(this@GameActivity, ScoreActivity::class.java)
                intent.putIntegerArrayListExtra("wrongWordList", wrongWordList)
                intent.putExtra("score", score)
                intent.putExtra("totalQuestions", totalQuestions)
                startActivity(intent)
            }
        }.start()
    }

    /**
     * A function that retrieves a randomly selected word from the database that has not yet been tested.
     */
    private fun getWord() {
        val dao = (application as CanStudyApp).db.wordDao()

        setDifficultyLevel(dao) {
            // Handle the case where all words have been tested
            if (repeatedWordList.size == wordList.size) {
                repeatedWordList.clear()
                startActivity(Intent(this@GameActivity, ScoreActivity::class.java))
            }

            wordList.shuffle()

            var wordID: Int
            var selectedList: ArrayList<Int> = ArrayList()

            // Get four random words
            for (i in 1..4) {
                if (i == 1) {
                    do {
                        randomIndex = Random.nextInt(0, wordList.size)
                        wordID = wordList[randomIndex].ID
                    } while (selectedList.contains(wordID) || repeatedWordList.contains(wordID))

                    binding.tvGameEnglishTranslation.text = wordList[randomIndex].getEnglishWord()
                    correctCantoAnswer = wordList[randomIndex].getCantoWord()
                    repeatedWordList.add(wordID)
                    binding.tvGameWordID.text = wordID.toString()

                } else {
                    do {
                        randomIndex = Random.nextInt(0, wordList.size)
                        wordID = wordList[randomIndex].ID
                    } while (selectedList.contains(wordID))

                }
                selectedList.add(wordID)
            }

            selectedList.shuffle()

            lifecycleScope.launch {
                binding.apply {
                    btnGameOptionA.text = dao.readCantoWordById(selectedList[0]).firstOrNull()?.getCantoWord()
                    btnGameOptionB.text = dao.readCantoWordById(selectedList[1]).firstOrNull()?.getCantoWord()
                    btnGameOptionC.text = dao.readCantoWordById(selectedList[2]).firstOrNull()?.getCantoWord()
                    btnGameOptionD.text = dao.readCantoWordById(selectedList[3]).firstOrNull()?.getCantoWord()
                }
            }
        }
    }

    private fun setDifficultyLevel(dao: WordDao, callback: () -> Unit) {
        when (difficultySetting) {
            "Easy" -> {
                getOneWordList(dao) { list ->
                    wordList = list
                    callback()
                }
            }
            "Medium" -> {
                getTwoOrThreeWordList(dao) { list ->
                    wordList = list
                    callback()
                }
            }
            "Hard" -> {
                getFourPlusWordList(dao) { list ->
                    wordList = list
                    callback()
                }
            }
            else -> {
                Log.e("Error", "Difficulty setting not set")
                callback()
            }
        }
    }

    /**
     * A function that describes how each option button should react when they are clicked.
     */
    override fun onClick(view: View?) {
        totalQuestions++

        val button = view as? Button ?: return
        val selectedCantoOption = button.text.toString()

        if (selectedCantoOption == correctCantoAnswer) {
            score++
        } else {
            wrongWordList.add(binding.tvGameWordID.text.toString().toInt())
        }

        getWord()
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
}