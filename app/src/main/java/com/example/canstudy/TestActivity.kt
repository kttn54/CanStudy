package com.example.canstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.databinding.ActivityTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Array
import kotlin.random.Random

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var ibTick: ImageButton
    private lateinit var ibCross: ImageButton
    private lateinit var btnCheck: Button
    private lateinit var btnReview: Button
    private lateinit var tvCantoneseDescription: TextView
    private lateinit var tvCantoneseTranslation: TextView
    private lateinit var tvEnglishDescription: TextView
    private lateinit var tvEnglishTranslation: TextView
    private lateinit var tvLeftScore: TextView
    private lateinit var tvRightScore: TextView
    private var correctScore = 0
    private var totalScore = 0
    private lateinit var wordsChosen: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        val dao = (application as CanStudyDatabaseApp).db.wordDao()

        ibTick.setOnClickListener { calculateCorrectAnswer() }
        ibCross.setOnClickListener { calculateWrongAnswer() }
        btnCheck.setOnClickListener { displayCantoneseText() }
    }

    private fun calculateCorrectAnswer() {
        correctScore++
        totalScore++
        tvLeftScore.text = correctScore.toString()
        tvRightScore.text = totalScore.toString()
    }

    private fun calculateWrongAnswer() {
        totalScore++
        tvRightScore.text = totalScore.toString()
    }

    private fun displayCantoneseText() {
        tvCantoneseDescription.visibility = VISIBLE
        tvCantoneseTranslation.visibility = VISIBLE
    }

    private fun initialiseActivity() {
        ibTick = binding.ibTick
        ibCross = binding.ibCross
        btnCheck = binding.btnCheck
        btnReview = binding.btnReview
        tvCantoneseDescription = binding.tvCantoneseDescription
        tvCantoneseTranslation = binding.tvCantoneseTranslation
        tvEnglishDescription = binding.tvEnglishDescription
        tvEnglishTranslation = binding.tvEnglishTranslation
        tvLeftScore = binding.tvLeftScore
        tvRightScore = binding.tvRightScore

        tvCantoneseTranslation.visibility = INVISIBLE
        tvCantoneseDescription.visibility = INVISIBLE

        val dao = (application as CanStudyDatabaseApp).db.wordDao()

        getWordList(dao) { wordList ->
            wordsChosen = ArrayList(wordList.size)
            val randomValue = Random.nextInt(0, wordList.size - 1)
            tvEnglishTranslation.text = wordList[randomValue].getEnglishWord()
            tvCantoneseTranslation.text = wordList[randomValue].getCantoneseWord()
            wordsChosen.add(randomValue)
            //Log.e("sushi", "$wordsChosen")
        }
    }

    private fun getWord() {
        val dao = (application as CanStudyDatabaseApp).db.wordDao()
        getWordList(dao) { wordList ->
            wordsChosen = ArrayList(wordList.size)
            do {
                val randomValue = Random.nextInt(0, wordList.size - 1)
                tvEnglishTranslation.text = wordList[randomValue].getEnglishWord()
                tvCantoneseTranslation.text = wordList[randomValue].getCantoneseWord()
                wordsChosen.add(randomValue)
            } while(!wordsChosen.contains(randomValue))
            //Log.e("sushi", "$wordsChosen")
        }
    }

    private fun getWordList(wordDao: WordDao, callback: (ArrayList<WordModel>) -> Unit) {
        val wordList = ArrayList<WordModel>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordModel(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.CORRECT_STATUS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }



}