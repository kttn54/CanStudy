package com.example.canstudy.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CantoWords")
data class WordEntity(
    @PrimaryKey(autoGenerate=true)
    val ID: Int,
    val CANTO_WORD: String,
    val ENGLISH_WORD: String,
    val NEW_STATUS: Int,
    val NUMBER_OF_WORDS: Int
) {
    fun getId(): Int {
        return ID
    }

    fun getCantoWord(): String {
        return CANTO_WORD
    }

    fun getEnglishWord(): String {
        return ENGLISH_WORD
    }

    fun getNewStatus(): Int {
        return NEW_STATUS
    }

    fun getNumberOfWords(): Int {
        return NUMBER_OF_WORDS
    }
}



