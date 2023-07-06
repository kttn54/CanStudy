package com.example.canstudy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.R
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//TODO: create dimens, strings and colors for all the activity layouts

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * A function that gets a word from the database of words.
     */
    fun getAllWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordEntity(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.NEW_STATUS,
                            word.NUMBER_OF_WORDS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }

    fun getOneWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readWordByLengthOneWord().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordEntity(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.NEW_STATUS,
                            word.NUMBER_OF_WORDS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }

    fun getTwoOrThreeWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readWordByLengthTwoOrThreeWords().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordEntity(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.NEW_STATUS,
                            word.NUMBER_OF_WORDS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }

    fun getFourPlusWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readWordByLengthFourPlusWords().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    for (word in allWordsList) {
                        val newWord = WordEntity(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.NEW_STATUS,
                            word.NUMBER_OF_WORDS
                        )
                        wordList.add(newWord)
                    }
                    callback(wordList)
                }
            }
        }
    }
}