package com.example.canstudy.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class TestViewModel @Inject constructor(
    private val wordDao: WordDao
): ViewModel() {

    val currentWord = MutableLiveData<WordEntity>()
    val score = MutableLiveData<Pair<Int, Int>>()
    private var repeatedWords = ArrayList<Int>()
    private var wrongWordList = ArrayList<Int>()
    private var randomIndex: Int = Random.nextInt()

    fun getWord() {
        getAllWordList(wordDao) { wordList ->
            if (repeatedWords.size == wordList.size) {
                tvCantoneseDescription.visibility = VISIBLE
                tvCantoneseTranslation.visibility = VISIBLE
                addRestartReviewDialog()
                disableElements()
                repeatedWords.clear()
                return@getAllWordList
            }
        }

        viewModelScope.launch {
            val wordList = getAllWordList()
            val randomIndex = Random.nextInt(0, wordList.size)
            val word = wordList[randomIndex]
            currentWord.value = word
        }
    }

    fun getAllWordList(wordDao: WordDao, callback: (ArrayList<WordEntity>) -> Unit) {
        val wordList = ArrayList<WordEntity>()
        viewModelScope.launch {
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
        viewModelScope.launch {
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

    // Call this function when the user correctly guesses a word.
    fun correctAnswer() {
        val currentScore = score.value ?: Pair(0, 0)
        score.value = Pair(currentScore.first + 1, currentScore.second + 1)
    }

    // Call this function when the user incorrectly guesses a word.
    fun wrongAnswer() {
        val currentScore = score.value ?: Pair(0, 0)
        score.value = Pair(currentScore.first, currentScore.second + 1)
    }

    // Call this function to reset the score to (0, 0).
    fun resetScore() {
        score.value = Pair(0, 0)
    }

}