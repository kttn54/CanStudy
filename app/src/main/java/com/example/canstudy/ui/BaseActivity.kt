package com.example.canstudy.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.canstudy.R
import com.example.canstudy.databinding.DialogExitGameBinding
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * A function that gets a word from the database of words.
     */
    fun getWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
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


}