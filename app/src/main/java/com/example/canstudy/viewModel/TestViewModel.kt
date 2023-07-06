package com.example.canstudy.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun getWord() {
        viewModelScope.launch {
            val wordList = getWordList()
            val randomIndex = Random.nextInt(0, wordList.size)
            val word = wordList[randomIndex]
            currentWord.value = word
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

    // Use suspend function to fetch wordList
    private suspend fun getWordList(): List<WordEntity> = withContext(Dispatchers.IO) {
        val wordList = mutableListOf<WordEntity>()
        wordDao.readAll().collect { wordList.addAll(it) }
        wordList
    }
}