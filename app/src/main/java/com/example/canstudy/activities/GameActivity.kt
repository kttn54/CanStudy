package com.example.canstudy.activities

import android.app.Dialog
import android.content.Intent
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.databinding.ActivityGameBinding
import com.example.canstudy.databinding.DialogExitGameBinding
import com.example.canstudy.CanStudyApp
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

    private var difficultySetting = ""
    private var countdownTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null
    private var countdownTime = 3
    private var gameTime = 0
    private var score = 0
    private var totalQuestions = 0
    private var wordList: ArrayList<WordEntity> = arrayListOf()
    private var repeatedWordList = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var selectedList: ArrayList<Int> = ArrayList()
    private var randomIndex: Int = Random.nextInt()
    private var englishWord = ""
    private var correctCantoAnswer = ""
    private var isResumed = false
    private var timeLeftOnTimer = 0
    private var wordId = 0

    private lateinit var progressBar: ProgressBar

    companion object {
        private val CORRECT_SCORE = "CORRECT_SCORE"
        private val TOTAL_QUESTIONS = "TOTAL_QUESTIONS"
        private val ENGLISH_WORD = "ENGLISH_WORD"
        private val CORRECT_CANTO_ANSWER = "CORRECT_CANTO_ANSWER"
        private val WORD_ID = "WORD_ID"
        private val REPEATED_WORDS = "REPEATED_WORDS"
        private val WRONG_WORDS = "WRONG_WORDS"
        private val SELECTED_WORDS = "SELECTED_WORDS"
        private val TIME_LEFT_ON_TIMER = "TIME_LEFT_ON_TIMER"
        private val IS_RESUMED = "IS_RESUMED"
        private val DIFFICULTY_SETTING = "DIFFICULTY_SETTING"
        private val GAME_TIME = "GAME_TIME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(CORRECT_SCORE)
            totalQuestions = savedInstanceState.getInt(TOTAL_QUESTIONS)
            gameTime = savedInstanceState.getInt(GAME_TIME)
            englishWord = savedInstanceState.getString(ENGLISH_WORD).toString()
            wordId = savedInstanceState.getInt(WORD_ID)
            difficultySetting = savedInstanceState.getString(DIFFICULTY_SETTING).toString()
            binding.tvGameEnglishTranslation.text = englishWord
            correctCantoAnswer = savedInstanceState.getString(CORRECT_CANTO_ANSWER).toString()
            repeatedWordList = savedInstanceState.getIntegerArrayList(REPEATED_WORDS) as ArrayList<Int>
            wrongWordList = savedInstanceState.getIntegerArrayList(WRONG_WORDS) as ArrayList<Int>
            selectedList = savedInstanceState.getIntegerArrayList(SELECTED_WORDS) as ArrayList<Int>
            timeLeftOnTimer = savedInstanceState.getInt(TIME_LEFT_ON_TIMER)
            isResumed = savedInstanceState.getBoolean(IS_RESUMED)
        }

        initialiseActivity()

        binding.btnGameOptionA.setOnClickListener(this)
        binding.btnGameOptionB.setOnClickListener(this)
        binding.btnGameOptionC.setOnClickListener(this)
        binding.btnGameOptionD.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        isResumed = true
        outState.putInt(CORRECT_SCORE, score)
        outState.putInt(TOTAL_QUESTIONS, totalQuestions)
        outState.putString(ENGLISH_WORD, englishWord)
        outState.putString(CORRECT_CANTO_ANSWER, correctCantoAnswer)
        outState.putInt(GAME_TIME, gameTime)
        outState.putInt(WORD_ID, wordId)
        outState.putString(DIFFICULTY_SETTING, difficultySetting)
        outState.putIntegerArrayList(REPEATED_WORDS, repeatedWordList)
        outState.putIntegerArrayList(WRONG_WORDS, wrongWordList)
        outState.putIntegerArrayList(SELECTED_WORDS, selectedList)
        outState.putInt(TIME_LEFT_ON_TIMER, timeLeftOnTimer)
        outState.putBoolean(IS_RESUMED, isResumed)
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
        progressBar = binding.progressBarGame

        if (!isResumed) {
            val intent = intent
            difficultySetting = intent.getStringExtra("difficultySetting").toString()
            gameTime = intent.getIntExtra("gameTime", 0)

            setupGame()
        } else {
            startGame()
        }
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
        var currentTime: Int

        if (isResumed) {
            currentTime = timeLeftOnTimer
            binding.tvGameWordID.text = wordId.toString()
            setButtonOptionText()
        } else {
            currentTime = gameTime
            getWord()
        }

        gameTimer = object : CountDownTimer((currentTime * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                timeLeftOnTimer = currentTime
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

            // Get four random words
            for (i in 1..4) {
                if (i == 1) {
                    do {
                        randomIndex = Random.nextInt(0, wordList.size)
                        wordId = wordList[randomIndex].ID
                    } while (selectedList.contains(wordId) || repeatedWordList.contains(wordId))

                    englishWord = wordList[randomIndex].getEnglishWord()
                    binding.tvGameEnglishTranslation.text = englishWord

                    correctCantoAnswer = wordList[randomIndex].getCantoWord()
                    repeatedWordList.add(wordId)
                    binding.tvGameWordID.text = wordId.toString()

                } else {
                    do {
                        randomIndex = Random.nextInt(0, wordList.size)
                        wordId = wordList[randomIndex].ID
                    } while (selectedList.contains(wordId))

                }
                selectedList.add(wordId)
            }

            selectedList.shuffle()
            setButtonOptionText()
        }
    }

    private fun setButtonOptionText() {
        val dao = (application as CanStudyApp).db.wordDao()
        lifecycleScope.launch {
            binding.apply {
                btnGameOptionA.text = dao.readCantoWordById(selectedList[0]).firstOrNull()?.getCantoWord()
                btnGameOptionB.text = dao.readCantoWordById(selectedList[1]).firstOrNull()?.getCantoWord()
                btnGameOptionC.text = dao.readCantoWordById(selectedList[2]).firstOrNull()?.getCantoWord()
                btnGameOptionD.text = dao.readCantoWordById(selectedList[3]).firstOrNull()?.getCantoWord()
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

    override fun onDestroy() {
        super.onDestroy()
        gameTimer?.cancel()
        countdownTimer?.cancel()
    }
}